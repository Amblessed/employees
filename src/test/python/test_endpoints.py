
import pytest
import allure
from utilities import load_json_file, run_request, RequestType
from db_connection import get_employee_from_db, get_all_employees_from_db
from requests.auth import HTTPBasicAuth
import requests, json
from pathlib import Path



test_cases = load_json_file("testcases.json")
testcases_get_by_id = load_json_file("get_employee_by_id.json")
testcases_get_all = load_json_file("get_all_employees.json")
test_cases_delete = load_json_file("testcases_delete.json")
test_cases_security = load_json_file("testcases_security.json")
# users_data = load_json_file("user_details.json")


# ------------------- GENERIC SECURITY TEST WITH SEVERITY -------------------
@pytest.mark.skip(reason="Not implemented")
@pytest.mark.security
@pytest.mark.parametrize("case", test_cases_security)
def test_generic_security_employee(case):
    """
    Generic SECURITY test for Employee API with dynamic Allure labels and severity.
    """
    run_request(RequestType(case["method"]), case)

# ------------------- GENERIC GET TEST WITH SEVERITY -------------------
@pytest.mark.get
@pytest.mark.parametrize("case", testcases_get_by_id)
def test_get_employee_by_id(case):
    """
    Generic GET test for Employees API with dynamic Allure labels and severity.
    """
    response, case = run_request(RequestType.GET, case)
    response_json = response.json()

    # Case: Get employee by ID
    employee_id = case.get("endpoint").split("/")[-1]
    employee_from_db = get_employee_from_db(employee_id)

    # Negative test
    if case.get("type") == "Negative Test":
        assert response.status_code == case.get("expected_status"), "Get Employee By ID test failed"
        assert response_json.get("detail") == case.get("expected_detail"), "Get Employee By ID test failed"
        return

    # Positive test
    employee = response_json.get("employee")
    for key in ["firstName", "lastName", "email"]:
        assert employee.get(key) == employee_from_db.get(key), f"Get Employee By ID test failed for {key}"

# ------------------- GENERIC GET ALL EMPLOYEES TEST WITH SEVERITY -------------------
@pytest.mark.getall
@pytest.mark.parametrize("case", testcases_get_all)
def test_get_all_employees(case):
    """
    Generic GET test for Employees API with dynamic Allure labels and severity.
    """
    response, case = run_request(RequestType.GET, case)
    response_json = response.json()
    if case.get("type") == "Negative Test":
        assert response.status_code == case.get("expected_status"), "Get All Employees test failed"
        assert response_json.get("detail") == case.get("expected_detail"), "Get All Employees test failed"
        return
    employees = response_json.get("employees")
    assert employees is not None, "Response should contain 'employees' key"
    assert isinstance(employees, list)

    print(response_json.get("size"))

    page_size = int(case["params"]["pageSize"]) if case.get("params") else case["pageSize"]
    assert len(employees) == page_size, "Get All Employees test failed"



# ------------------- GENERIC CREATE EMPLOYEE TEST WITH SEVERITY -------------------
@pytest.mark.skip(reason="Not implemented")
@pytest.mark.create
@pytest.mark.parametrize("case", test_cases.get("POST"))
def test_generic_create_employee(case):
    """
    Generic CREATE test for Employee API with dynamic Allure labels and severity.
    """
    count_before_request = len(get_all_employees_from_db())

    run_request(RequestType.POST, case)
    if case["type"] == "Positive Test":
        print(f"Count before request: {count_before_request}")
        print(f"Count after request: {len(get_all_employees_from_db())}")
        assert len(get_all_employees_from_db()) == count_before_request + 1, "Employee not created in DB"


# ------------------- GENERIC UPDATE EMPLOYEE TEST WITH SEVERITY -------------------
@pytest.mark.skip(reason="Not implemented")
@pytest.mark.put
@pytest.mark.parametrize("case", test_cases.get("PUT"))
def test_generic_update_employee(case):
    """
    Generic UPDATE test for Employee API with dynamic Allure labels and severity.
    """
    passed_case, response = run_request(RequestType.PUT, case)

    employee_id = passed_case["endpoint"].split("/")[-1]
    payload = passed_case["payload"]

    # Fetch DB result once for both negative and positive tests
    db_result = get_employee_from_db(employee_id)
    allure.attach(str(db_result), name="DB Query Result", attachment_type=allure.attachment_type.TEXT)

    if passed_case["type"] == "Negative Test":
        assert db_result is None, f"Employee {employee_id} still exists in DB"
    else:
        with allure.step("Verify employee is updated in the database"):
            assert db_result == payload and response.json().get("old_employee") != db_result, f"Employee {employee_id} not updated in DB"



# ------------------- GENERIC DELETE EMPLOYEE TEST WITH SEVERITY -------------------
@pytest.mark.skip(reason="Not implemented")
@pytest.mark.delete
@pytest.mark.parametrize("case", test_cases_delete)
def test_generic_delete_employee(case):
    """
    Generic DELETE test for Employee API with dynamic Allure labels and severity.
    """
    _, passed_case = run_request(RequestType.DELETE, case)
    print(passed_case)
    if passed_case["type"] == "Negative Test":
        return
    with allure.step("Verify employee no longer exists in the database"):
        db_result = get_employee_from_db(1)
        allure.attach(str(db_result), name="DB Query Result", attachment_type=allure.attachment_type.TEXT)
        assert db_result is None, f"Employee {1} still exists in DB"



def load_user_details():
    path = Path("src/test/resources/user_details.json")
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


#@pytest.mark.parametrize("user_id, details", load_user_details().items())
def test_user_can_access_own_record(user_id, details):
    # email = details["email"]
    password = details["password"]
    role = details["role"]

    base_url = "http://localhost:9090/api/employees/id/"
    timeout = 15

    url = f"{base_url}{user_id}"
    auth = HTTPBasicAuth(user_id, password)
    response = requests.get(url, auth=auth, timeout=timeout)

    if role == "ROLE_EMPLOYEE":
        assert response.status_code == 200, f"Employee {user_id} failed to access own record"
        assert "employee" in response.json()
        assert response.json().get("detail") == "Employee found successfully"
    elif role in ("ROLE_MANAGER", "ROLE_ADMIN"):
        assert response.status_code == 200, f"{role} {user_id} failed to access record"
        assert "employee" in response.json()
    else:
        pytest.fail(f"Unknown role for user {user_id}: {role}")








