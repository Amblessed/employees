import json
from typing import Dict, Optional, Any
from requests import Response, JSONDecodeError
import allure
import requests
import os
import random
from enum import Enum
from faker import Faker
from requests.auth import HTTPBasicAuth
from db_connection import get_employee_from_db, get_all_employees_from_db, get_all_ids_from_db

BASE_URL = "http://localhost:9090/api/employees"
TIMEOUT = 20


class RequestType(str, Enum):
    GET = "GET"
    POST = "POST"
    PUT = "PUT"
    DELETE = "DELETE"


def _generate_random_employee() -> Dict[str, str]:
    fake = Faker()
    first_name = fake.first_name()
    last_name = fake.last_name()
    domains = ["gmail.com", "yahoo.com", "hotmail.com", "outlook.com"]
    domain = random.choice(domains)
    email = f"{first_name.lower()}.{last_name.lower()}@{domain}"
    return {
        "firstName": first_name,
        "lastName": last_name,
        "email": email,
    }


def run_request(request_type: RequestType, case: dict) -> tuple[dict, Response] | None:
    random_valid_id = random.choice(get_all_ids_from_db())
    random_invalid_id = random.randint(80000, 90000)
    random_negative_id = random.randint(-22, -1)
    replacements = {
        "RANDOM_VALID_ID": f"/{random_valid_id}",
        "RANDOM_INVALID_ID": f"/{random_invalid_id}",
        "RANDOM_NEGATIVE_ID": f"/{random_negative_id}",
    }
    endpoint = case.get("endpoint")
    if endpoint in replacements:
        case["endpoint"] = replacements[endpoint]

    is_security_test = "access" in case.get("story").lower() or "authentication" in case.get("story").lower()
    feature = "Security Test" if is_security_test else f"{request_type.name.capitalize()} Employee"

    with allure.step(assign_severity(case, feature)):
        if isinstance(case.get("expected_detail"), str):
            case["expected_detail"] = (
                case["expected_detail"]
                .replace("RANDOM_INVALID_ID", str(random_invalid_id))
                .replace("RANDOM_VALID_ID", str(random_valid_id))
                .replace("RANDOM_NEGATIVE_ID", str(random_negative_id))
            )

        if case["payload"] == "RANDOM_EMPLOYEE":
            case["payload"] = _generate_random_employee()
        if case["payload"] == "EXISTING_EMAIL":
            case["payload"] = get_employee_from_db(random.randint(71, 86))

        response = make_request(request_type, case)
        validate_response(response, case)
        attach_response_body(response)

    return case, response


def _print_response(response):
    try:
        print(json.dumps(response.json(), indent=4, sort_keys=True))
    except JSONDecodeError:
        print(response.text)


def _are_all_strings(params: Dict) -> bool:
    """Check if all keys and values in params are strings.
    Args:
        params: Dictionary to validate
    Returns:
        bool: True if all keys and values are strings, False otherwise
    """
    return all(isinstance(k, str) and isinstance(v, str)
               for k, v in params.items())


def _validate_request_params(params: Optional[Dict]):
    """Validate request parameters.

    Args:
        params: Query parameters to validate

    Raises:
        TypeError: If params is not a dict
        ValueError: If params contains non-string keys/values
    """
    if params is None:
        return

    if not isinstance(params, dict):
        raise TypeError(f"params must be a dict, got {type(params).__name__}")

    if not _are_all_strings(params):
        raise ValueError("All keys and values in params must be strings")


def validate_response(response: requests.Response, case: Dict[str, Any]):

    _validate_status_code(response, case["expected_status"])
    is_positive_test: bool = case["type"] == "Positive Test"
    _validate_positive_response(response, case) if is_positive_test else _validate_negative_response(response, case)

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


def assign_severity(case: Dict, feature: str) -> str:
    """Assign severity level based on test case type."""
    is_positive_test = case["type"] == "Positive Test"
    severity = allure.severity_level.NORMAL if is_positive_test else allure.severity_level.CRITICAL
    badge = "✅" if case["type"] == "Positive Test" else "❌"

    # Dynamic labels for Allure
    allure.dynamic.feature(feature)
    allure.dynamic.story(case["story"])
    allure.dynamic.title(case["story"].replace(" ", "_").lower())
    allure.dynamic.severity(severity)

    step_title = (
        f"{badge} {case['type']}: {case['story']} | "
        f"Endpoint={case['endpoint']} | Params={case['params'] or 'None'} | "
        f"Expected Status={case['expected_status']}"
    )

    return step_title


def make_request(method: str, case: Dict) -> requests.Response | None:
    """
    Makes an HTTP request with proper handling of params and json payloads.

    Args:
        method: HTTP method (GET, POST, etc.)
        case: Test case dictionary

    Returns:
        requests.Response object
    """
    params = case.get('params')
    _validate_request_params(case.get('params'))

    json_data = case.get('payload')

    if json_data is not None and not isinstance(json_data, dict):
        raise TypeError(f"json_data must be a dict, got {type(json_data).__name__}")

    url = f"{BASE_URL}{case.get('endpoint')}"
    print(f"\nURL: {url}")


    user = case.get("user")
    auth = HTTPBasicAuth(user, "fun123")

    try:
        request_kwargs = {'method': method, 'url': url, 'params': params or {}, 'timeout': TIMEOUT, 'auth': auth}

        # Only add json payload for non-GET/DELETE methods
        if method.upper() not in ('GET', 'DELETE') and json_data is not None:
            request_kwargs['json'] = json_data
            request_kwargs['headers'] = {'Content-Type': 'application/json'}

        response = requests.request(**request_kwargs)
        if method.upper() == "DELETE":
            _print_response(response)
        return response
    except Exception as e:
        print(f"Error making {method} request to {url}: {str(e)}")
        raise


def validate_negative_response_detail(response: requests.Response, case: Dict):
    """Helper to validate error details in negative test cases."""
    if not case:
        return

    response_data = response.json()
    assert response_data.get("detail") == case, f"Expected detail: {case}, Actual: {response_data.get('detail')}"

def _validate_status_code(response: requests.Response, status_code: int):
    """Helper to validate status code in response body."""
    actual_status = response.status_code
    assert actual_status == status_code, (
        f"Unexpected status code => Expected: {status_code}, Actual: {actual_status}"
    )

def validate_positive_response_detail(response: requests.Response, case: Dict):
    #"""Helper to validate error details in negative test cases."""
    # Validate data for positive responses
   books_array = response.json().get("books", [])
   if case:
        value = "category" if "category" in case else "title"
        for data in books_array:
            assert data[value] == case[value], f"Book {value} mismatch for {case}"


def attach_response_body(response: requests.Response):
    allure.attach(
        response.text,
        name="Response Body",
        attachment_type=allure.attachment_type.JSON
    )


def load_test_cases(file_name: str) -> dict:
    """
    Reads a JSON file containing test cases and returns it as a dictionary.

    :param file_name: Path to the JSON file
    :return: Dictionary with test cases
    """
    from pathlib import Path

    file_path = find_absolute_file_path(file_name)[0]
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            data = json.load(file)
        return data
    except FileNotFoundError:
        print(f"Error: File not found at {file_path}")
        return {}
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON: {e}")
        return {}

def find_absolute_file_path(filename: str, search_path: str = ".") -> list:
    """
    Search for a file by name and return a list of absolute paths.
    """
    matches = []
    for root, _, files in os.walk(search_path):
        if filename in files:
            matches.append(os.path.abspath(os.path.join(root, filename)))
    return matches