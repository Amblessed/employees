
import pytest
import allure
from utils import load_test_cases, run_request, RequestType
from db_connection import get_employee_from_db, get_all_employees_from_db

test_cases = load_test_cases("testcases.json")

# ------------------- GENERIC SECURITY TEST WITH SEVERITY -------------------
@pytest.mark.order(2)
@pytest.mark.security
@pytest.mark.parametrize("case", test_cases.get("SECURITY"))
def test_generic_security_employee(case):
    """
    Generic SECURITY test for Employee API with dynamic Allure labels and severity.
    """
    run_request(RequestType(case["method"]), case)

# ------------------- GENERIC GET TEST WITH SEVERITY -------------------
@pytest.mark.order(3)
@pytest.mark.get
@pytest.mark.parametrize("case", test_cases.get("GET"))
def test_generic_get_employees(case):
    """
    Generic GET test for Employees API with dynamic Allure labels and severity.
    """
    case, response = run_request(RequestType.GET, case)
    if case.get("story") == "Get All Employees":
        response_result = response.json().get("employees")
        assert len(response_result) == len(get_all_employees_from_db()), "Get All Employees test failed"
    else:
        employee_id = case.get("endpoint").split("/")[-1]
        if case.get("type") == "Negative Test":
            assert response.json().get("employee") == get_employee_from_db(employee_id), "Get Employee By ID test failed"
            return
        response_result = response.json().get("employee")
        employee = get_employee_from_db(employee_id)
        assert response_result.get("firstName") == employee.get("firstName"), "Get Employee By ID test failed"
        assert response_result.get("lastName") == employee.get("lastName"), "Get Employee By ID test failed"
        assert response_result.get("email") == employee.get("email"), "Get Employee By ID test failed"


# ------------------- GENERIC CREATE EMPLOYEE TEST WITH SEVERITY -------------------
@pytest.mark.skip(reason="Not implemented")
@pytest.mark.order(4)
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
@pytest.mark.order(5)
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
@pytest.mark.order(1)
@pytest.mark.delete
@pytest.mark.parametrize("case", test_cases.get("DELETE"))
def test_generic_delete_employee(case):
    """
    Generic DELETE test for Employee API with dynamic Allure labels and severity.
    """
    passed_case, _ = run_request(RequestType.DELETE, case)
    employee_id = passed_case["endpoint"].split("/")[-1]
    if passed_case["type"] == "Negative Test":
        return
    with allure.step("Verify employee no longer exists in the database"):
        db_result = get_employee_from_db(employee_id)
        allure.attach(str(db_result), name="DB Query Result", attachment_type=allure.attachment_type.TEXT)
        assert db_result is None, f"Employee {employee_id} still exists in DB"










