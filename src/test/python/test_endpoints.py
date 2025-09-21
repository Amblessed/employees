
import pytest
import allure
from utilities import load_json_file, run_request, RequestType, validate_employees
from db_connection import get_employee_from_db, get_all_employees_from_db, get_all_employees_search_from_db

test_cases = load_json_file("testcases.json")
testcases_get_by_id = load_json_file("get_employee_by_id.json")
testcases_get_all = load_json_file("get_all_employees.json")
testcases_get_all_search = load_json_file("get_all_employees_search.json")
test_cases_delete = load_json_file("delete_employee_by_id.json")
test_cases_security = load_json_file("testcases_security.json")






# ------------------- GENERIC SECURITY TEST WITH SEVERITY -------------------
@pytest.mark.security
@pytest.mark.parametrize("case", test_cases_security)
def test_security_employee(case):
    """
    Generic SECURITY test for Employee API with dynamic Allure labels and severity.
    """
    run_request(RequestType(case["method"]), case, feature="Security Test")

# ------------------- GENERIC GET TEST WITH SEVERITY -------------------
@pytest.mark.get
@pytest.mark.parametrize("case", testcases_get_by_id)
def test_get_employee_by_id(case):
    """
    Generic GET test for Employees API with dynamic Allure labels and severity.
    """
    response, case = run_request(RequestType.GET, case, feature="Get Employee By ID")
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
    response, case = run_request(RequestType.GET, case, feature="Get All Employees")
    response_json = response.json()
    if case.get("type") == "Negative Test":
        assert response.status_code == case.get("expected_status"), "Get All Employees test failed"
        assert response_json.get("detail") == case.get("expected_detail"), "Get All Employees test failed"
        return
    employees = response_json.get("employees")
    assert employees is not None, "Response should contain 'employees' key"
    assert isinstance(employees, list), "'employees' should be a list"

    page_size = int(case["params"]["size"]) if case.get("params") else case["size"]
    assert len(employees) == page_size, "Get All Employees test failed"


# ------------------- GENERIC GET ALL EMPLOYEES BY SEARCH TEST WITH SEVERITY -------------------
@pytest.mark.getallsearch
@pytest.mark.parametrize("case", testcases_get_all_search)
def test_get_all_employees_by_search(case):
    """
    Generic GET test for Employees API with dynamic Allure labels and severity.
    """
    response, case = run_request(RequestType.GET, case, feature="Get All Employees By Search")
    response_body = response.json()
    if case.get("type") == "Negative Test":
        assert response.status_code == case.get("expected_status"), "Get All Employees test failed"
        assert response_body.get("detail") == case.get("expected_detail"), "Get All Employees test failed"
        return


    employees = response_body.get("employees")
    assert employees is not None, "Response should contain 'employees' key"
    assert isinstance(employees, list), "'employees' should be a list"

    # Prepare expected values with defaults
    params = case.get("params") or {}
    expected_values = {
        "department": params.get("department", ""),
        "position": params.get("position", ""),
        "salary": params.get("salary", 50000)
    }

    # Validate API response
    validate_employees(employees, expected_values, source="API")

    # Validate DB results
    database_result = get_all_employees_search_from_db(
        expected_values.get("department"),
        expected_values.get("position"),
        expected_values.get("salary")
    )
    validate_employees(database_result, expected_values, source="DB")



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
@pytest.mark.delete
@pytest.mark.parametrize("case", test_cases_delete)
def test_generic_delete_employee(case):
    """
    Generic DELETE test for Employee API with dynamic Allure labels and severity.
    """
    _, passed_case = run_request(RequestType.DELETE, case, feature="Delete Employee By ID")
    print(passed_case)
    if passed_case["type"] == "Negative Test":
        return
    with allure.step("Verify employee no longer exists in the database"):
        employee_id = passed_case["endpoint"].split("/")[-1]
        db_result = get_employee_from_db(employee_id)
        allure.attach(str(db_result), name="DB Query Result", attachment_type=allure.attachment_type.TEXT)
        assert db_result is None, f"Employee {employee_id} still exists in DB"

@pytest.mark.order(1)
@pytest.mark.getownrecord
def test_user_can_access_own_record():
    run_request(RequestType.GET, case={}, feature="User Can Access Own Record")










