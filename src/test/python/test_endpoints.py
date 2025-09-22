
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
test_cases_create = load_json_file("create_employee.json")


# Merge test data with scenario labels
all_get_cases = [
    *[(case | {"scenario": "by_id"}) for case in testcases_get_by_id],
    *[(case | {"scenario": "get_all"}) for case in testcases_get_all],
    *[(case | {"scenario": "search"}) for case in testcases_get_all_search],
]


# ------------------- HELPERS -------------------
def assert_negative(response, case, feature):
    response_json = response.json()
    assert response.status_code == case["expected_status"], f"{feature} failed"
    assert response_json.get("detail") == case["expected_detail"], f"{feature} failed"


def extract_employee_id(endpoint: str) -> str:
    return endpoint.split("/")[-1]


# ------------------- UNIFIED GET TEST -------------------
@pytest.mark.get
@pytest.mark.parametrize("case", all_get_cases)
def test_get_employees(case):
    """
    Unified GET tests: by_id, get_all, and search.
    """
    scenario = case["scenario"]
    response, case = run_request(RequestType.GET, case, feature=f"Get Employees - {scenario}")
    response_json = response.json()

    if case["type"] == "Negative Test":
        assert_negative(response, case, f"Get Employees - {scenario}")
        return

    # Scenario-specific validations
    if scenario == "by_id":
        employee_id = extract_employee_id(case["endpoint"])
        db_employee = get_employee_from_db(employee_id)
        api_employee = response_json.get("employee")
        for key in ["firstName", "lastName", "email"]:
            assert api_employee.get(key) == db_employee.get(key), f"Mismatch on {key}"

    elif scenario == "get_all":
        employees = response_json.get("employees")
        assert isinstance(employees, list), "'employees' must be a list"
        page_size = int(case.get("params", {}).get("size", case.get("size")))
        assert len(employees) == page_size, "Unexpected employee count"

    elif scenario == "search":
        employees = response_json.get("employees")
        assert isinstance(employees, list), "'employees' must be a list"

        params = case.get("params", {})
        expected_values = {
            "department": params.get("department", ""),
            "position": params.get("position", ""),
            "salary": params.get("salary", 50000),
        }

        # Validate API vs. DB
        validate_employees(employees, expected_values, source="API")
        db_result = get_all_employees_search_from_db(
            expected_values["department"],
            expected_values["position"],
            expected_values["salary"],
        )
        validate_employees(db_result, expected_values, source="DB")



# ------------------- GENERIC SECURITY TEST WITH SEVERITY -------------------
@pytest.mark.security
@pytest.mark.parametrize("case", test_cases_security)
def test_security_employee(case):
    """
    Generic SECURITY test for Employee API with dynamic Allure labels and severity.
    """
    run_request(RequestType(case["method"]), case, feature="Security Test")


# ------------------- GENERIC CREATE EMPLOYEE TEST WITH SEVERITY -------------------
@pytest.mark.create
@pytest.mark.parametrize("case", test_cases_create)
def test_create_employee(case):
    """
    Generic CREATE test for Employee API with dynamic Allure labels and severity.
    """
    count_before_request = len(get_all_employees_from_db())

    _, case = run_request(RequestType.POST, case, feature="Create Employee Test")

    if case["type"] == "Positive Test":
        print(f"Count before request: {count_before_request}")
        print(f"Count after request: {len(get_all_employees_from_db())}")
        assert len(get_all_employees_from_db()) == count_before_request + 1, "Employee not created in DB"


# ------------------- GENERIC UPDATE EMPLOYEE TEST WITH SEVERITY -------------------
@pytest.mark.skip(reason="Not implemented")
@pytest.mark.put
@pytest.mark.parametrize("case", test_cases.get("PUT"))
def test_update_employee(case):
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
def test_delete_employee(case):
    """
    Generic DELETE test for Employee API with dynamic Allure labels and severity.
    """
    _, case = run_request(RequestType.DELETE, case, feature="Delete Employee By ID")
    print(case)
    if case["type"] == "Negative Test":
        return
    with allure.step("Verify employee no longer exists in the database"):
        employee_id = extract_employee_id(case["endpoint"])
        db_result = get_employee_from_db(employee_id)
        allure.attach(str(db_result), name="DB Query Result", attachment_type=allure.attachment_type.TEXT)
        assert db_result is None, f"Employee {employee_id} still exists in DB"

@pytest.mark.order(1)
@pytest.mark.getownrecord
def test_employees_can_access_own_record():
    run_request(RequestType.GET, case={}, feature="User Can Access Own Record")

    # Dynamic labels for Allure
    allure.dynamic.feature("Employees can access their own personal records")
    # allure.dynamic.story("Employees can access their own record")
    allure.dynamic.title("Employees can access their own record".replace(" ", "_").lower())
    allure.dynamic.severity(allure.severity_level.NORMAL)











