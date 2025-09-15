import json
import os
import random
from pathlib import Path
from typing import Dict, List, Tuple
from enum import Enum
from requests.auth import HTTPBasicAuth
import requests
import allure
import time

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


def run_request(request_type: RequestType, case: dict):
    """Example: run API request using employee details."""
    random_employees = get_employee_details(case["user"])
    employee_1, employee_2 = random_employees

    # Replace placeholders in endpoint/payload
    replacements = {
        "RANDOM_VALID_ID": f"/id/{employee_1['userId']}",
        "RANDOM_VALID_EMP_ID": f"/id/{employee_1['userId']}",
        "RANDOM_INVALID_ID": "/id/AAABBBCCC"
    }
    endpoint = case.get("endpoint")
    if endpoint in replacements:
        case["endpoint"] = replacements[endpoint]

    # Add authentication
    if case["user"] == "EmployeeValid":
        case["user"] = employee_2.get("userId")
        case["password"] = employee_2.get("password")
    else:
        case["user"] = employee_1.get("userId")
        case["password"] = employee_1.get("password")

    # Make request
    url = f"{BASE_URL}{case.get('endpoint')}"
    auth = HTTPBasicAuth(case.get("user"), case.get("password"))
    response = requests.request(
        method=request_type.value,
        url=url,
        json=case.get("payload") if request_type not in (RequestType.GET, RequestType.DELETE) else None,
        params=case.get("params") or {},
        timeout=TIMEOUT,
        auth=auth
    )

    # Attach response for Allure
    allure.attach(
        response.text,
        name="Response Body",
        attachment_type=allure.attachment_type.JSON
    )
    return response



if __name__ == "__main__":
    test_cases = load_json_file("testcases.json")
    test_cases_security = load_json_file("testcases_security.json")
    print(test_cases)
    print(test_cases_security)