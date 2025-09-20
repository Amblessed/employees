import json
import os
import pytest
import random
from pathlib import Path
from typing import Dict, List, Tuple, Any
from enum import Enum
from requests.auth import HTTPBasicAuth
import requests
import allure
from requests import Response, JSONDecodeError

BASE_URL = "http://localhost:9090/api/employees"
TIMEOUT = 20

# ---------------------------
# Enums & Helper Functions
# ---------------------------

class RequestType(str, Enum):
    GET = "GET"
    POST = "POST"
    PUT = "PUT"
    DELETE = "DELETE"


def get_search_dir() -> Path:
    """Locate src/test/resources folder (CI-friendly)."""
    env_dir = os.getenv("RESOURCE_DIR")
    if env_dir and Path(env_dir).exists():
        return Path(env_dir)

    candidate = Path("src/test/resources").resolve()
    if candidate.exists():
        return candidate

    current = Path(__file__).resolve()
    for parent in current.parents:
        candidate = parent / "src" / "test" / "resources"
        if candidate.exists():
            return candidate

    raise FileNotFoundError("Could not locate src/test/resources")


def find_file(filename: str) -> str:
    """Return first match of filename in resources."""
    search_dir = get_search_dir()
    for f in search_dir.rglob(Path(filename).name):
        return str(f)
    raise FileNotFoundError(f"Could not find {filename} in {search_dir}")


def load_json_file(file_name: str) -> dict:
    """
    Load JSON file and return dictionary.
    Prints the path for debug and handles missing/corrupt JSON safely.
    """
    file_path = find_file(file_name)
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            return json.load(f)
    except FileNotFoundError:
        print(f"File not found: {file_path}")
        return {}
    except json.JSONDecodeError as e:
        print(f"JSON decode error: {e}")
        return {}


def categorize_users(data: Dict[str, Dict[str, str]]) -> Tuple[List[Dict], List[Dict], List[Dict]]:
    """Categorize users into employees, managers, admins."""
    employees, managers, admins = [], [], []
    for key, val in data.items():
        user_dict = {
            "userId": key,
            "role": val.get("role"),
            "password": val.get("password"),
            "email": val.get("email")
        }
        role = val.get("role")
        if role == "ROLE_EMPLOYEE":
            employees.append(user_dict)
        elif role == "ROLE_MANAGER":
            managers.append(user_dict)
        else:
            admins.append(user_dict)
    return employees, managers, admins


# ---------------------------
# Core Functionality
# ---------------------------

def get_employee_details(user: str) -> List[Dict]:
    """Return two random employees/managers/admins based on user type."""
    users_file = find_file("user_details.json")
    users_data = load_json_file(users_file)
    employees, managers, admins = categorize_users(users_data)

    if user in ("Employee", "EmployeeValid"):
        pool = employees
    elif user == "Manager":
        pool = managers
    else:
        pool = admins

    return random.sample(pool, 2)


def get_all_users() -> Dict:
    """Return a single user's details based on user type."""
    users_file = find_file("user_details.json")
    users_data = load_json_file(users_file)
    return users_data


def run_request(request_type: RequestType, case: dict):
    """
    Run an API request using employee details from user_details.json.
    Handles role-based access, self vs other targets, and placeholder replacement.
    """

    users_data = get_all_users()



    if case == {}:
        allure_list = []
        for user_id,details in users_data.items():
            password = details["password"]
            role = details["role"]
            url = f"{BASE_URL}/id/{user_id}"
            auth = HTTPBasicAuth(user_id, password)
            response = requests.get(url, auth=auth, timeout=15)
            body = response.json()
            assert response.status_code == 200, f"{role} {user_id} failed to access record"
            assert "employee" in body
            allure_list.append(body)

            if role == "ROLE_EMPLOYEE":
                assert body.get("detail") == "Employee found successfully"
            elif role not in {"ROLE_MANAGER", "ROLE_ADMIN"}:
                pytest.fail(f"Unknown role for user {user_id}: {role}")

        allure.attach(str(allure_list), name="Employees can access own record", attachment_type=allure.attachment_type.TEXT)
        return

    # Filter users by role
    admins = [{"userId": k, **v} for k, v in users_data.items() if v["role"] == "ROLE_ADMIN"]
    managers = [{"userId": k, **v} for k, v in users_data.items() if v["role"] == "ROLE_MANAGER"]
    employees = [{"userId": k, **v} for k, v in users_data.items() if v["role"] == "ROLE_EMPLOYEE"]

    actor, target = get_access_target(case, employees, managers, admins)

    # Replace placeholders in endpoint and payload
    endpoint = case.get("endpoint", "")
    if endpoint in ("RANDOM_VALID_ID", "RANDOM_VALID_EMP_ID") and target:
        case["endpoint"] = f"/id/{target['userId']}"
    elif endpoint == "RANDOM_INVALID_ID":
        case["endpoint"] = "/id/AAABBBCCC"

    # Set authentication
    case["user"] = actor.get("userId")
    case["password"] = actor.get("password")

    # Construct request
    url = f"{BASE_URL}{case.get('endpoint', '')}"
    auth = HTTPBasicAuth(case.get("user"), case.get("password"))


    print(f"Params: {case["params"]}")

    response = requests.request(
        method=request_type.value,
        url=url,
        json=case.get("payload") if request_type not in (RequestType.GET, RequestType.DELETE) else None,
        params=case.get("params") or {},
        timeout=TIMEOUT,
        auth=auth
    )

    # Attach response to Allure
    allure.attach(response.text, name="Response Body", attachment_type=allure.attachment_type.JSON)

    # Print debug info
    print("\n============================== REQUEST ==============================")
    print(actor)
    print(target)
    print(f"Actor: {actor['userId']} ({actor['role']})")
    print(f"Target: {target['userId'] if target else 'ALL'}")
    print(f"URL: {url}")
    print(f"Method: {request_type.name}")
    print(f"Response: {response.status_code} {response.text}")
    print("=====================================================================\n")

    validate_response(response, case)
    return response, case

def _get_access_actor(case: Dict[str, Any], employees: List[Dict[str, Any]], managers: List[Dict[str, Any]], admins: List[Dict[str, Any]]):
    if case["user_role"] == "Employee":
        actor = random.choice(employees)
    elif case["user_role"] == "Manager":
        actor = random.choice(managers)
    elif case["user_role"] == "Admin":
        actor = random.choice(admins)
    else:  # Guest or unknown
        actor = {"userId": "guest", "password": ""}
    return actor

def get_access_target(case: Dict[str, Any], employees: List[Dict[str, Any]], managers: List[Dict[str, Any]], admins: List[Dict[str, Any]]):
    # Choose the user running the test
    actor = _get_access_actor(case, employees, managers, admins)

    # Choose the target employee for endpoints that require it
    if case.get("access_target") == "self":
        target = actor
    elif case.get("access_target") == "other":
        if actor["role"] == "ROLE_EMPLOYEE":
            # Employee should pick another employee (to test denial)
            target = random.choice([e for e in employees if e != actor])
        elif actor["role"] == "ROLE_MANAGER":
            target = random.choice([e for e in employees if e != actor])
        elif actor["role"] == "ROLE_ADMIN":
            target = random.choice([e for e in employees if e != actor])
        else:
            target = {"userId": "AAABBBCCC"}  # Invalid ID
    else:
        target = None  # For GET all employees
    return actor, target

def _print_response(response):
    try:
        print(json.dumps(response.json(), indent=4, sort_keys=True))
    except JSONDecodeError:
        print(response.text)


def validate_response(response: requests.Response, case: Dict[str, Any]):
    _validate_status_code(response, case["expected_status"])
    is_positive_test: bool = case["type"] == "Positive Test"
    _validate_positive_response(response, case) if is_positive_test else _validate_negative_response(response, case)

def _validate_status_code(response: requests.Response, status_code: int):
    """Helper to validate status code in response body."""
    #_print_response(response)
    actual_status = response.status_code
    assert actual_status == status_code, (
        f"Unexpected status code => Expected: {status_code}, Actual: {actual_status}"
    )


def _validate_positive_response(response: requests.Response, case: Dict[str, Any]):
    """Validate response data for positive test cases.

    Args:
        response: response
        case: Test case dictionary
    """
    if not case["check_field"]:
        return

    response_data = response.json()
    check_field = case["check_field"]
    data = response_data.get(check_field)
    if not data:
        return

    assert case['expected_detail'] == response_data.get("detail"), f"Detail mismatch: {case['expected_detail']} != {response_data.get('detail')}"
    assert check_field in response_data, f"Check field {check_field} not found in response"
    if isinstance(data, list):
        for employee in data:
            if case["payload"]:
                assert employee.get("firstName") == case["payload"]["firstName"], f"First name mismatch: {employee.get('firstName')} != {case["payload"]["firstName"]}"
                assert employee.get("lastName") == case["payload"]["lastName"], f"Last name mismatch: {employee.get('lastName')} != {case["payload"]["lastName"]}"
                assert employee.get("email") == case["payload"]["email"], f"Email mismatch: {employee.get('email')} != {case["payload"]["email"]}"
    else:
        assert len(data.get("firstName")) > 2, f"First name mismatch: {data.get('firstName')}"
        assert len(data.get("lastName")) > 2, f"Last name mismatch: {data.get('lastName')}"
        assert len(data.get("email")) > 2, f"Email mismatch: {data.get('email')}"

def _validate_negative_response(response: Response, case: Dict[str, Any]):
    """Validate response for negative test cases.

    Args:
        response: Response object
        case: Test case dictionary
    """
    response_body = response.json()
    if not response_body:
        return
    assert case['expected_detail'] == response_body.get("detail"), f"Detail mismatch for {case}"


if __name__ == "__main__":
    test_cases = load_json_file("testcases.json")
    test_cases_security = load_json_file("testcases_security.json")
    print(test_cases)
    print(test_cases_security)