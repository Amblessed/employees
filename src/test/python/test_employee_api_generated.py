import pytest
import requests
import json

# ==== Generated test for GET  ====

# Sure, here is a basic Pytest function that uses the 'requests' library to perform a GET request and includes assertions to check the HTTP status code and the basic structure of the response.


BASE_URL = "http://localhost:9090/employees"

@pytest.mark.parametrize(
    "page_number, page_size, sort_by, sort_direction", [(1, 10, 'firstName', 'asc')]
)
def test_get_employees(page_number, page_size, sort_by, sort_direction):
    params = {
        "pageNumber": page_number,
        "pageSize": page_size,
        "sortBy": sort_by,
        "sortDirection": sort_direction,
    }
    response = requests.get(BASE_URL, params=params)

    # Verify the response status code
    assert response.status_code == 200, f"Expected 200 but got {response.status_code} instead"

    response_body = response.json()

    # Basic structure of the response
    assert "employees" in response_body, "'employees' key not in response"
    assert isinstance(response_body["employees"], list), "'employees' value is not a list"
    
    for employee in response_body["employees"]:
        assert "firstName" in employee, "'firstName' key not in the employee data"
        assert "lastName" in employee, "'lastName' key not in the employee data"
        assert "email" in employee, "'email' key not in the employee data"
        assert "phone" in employee, "'phone' key not in the employee data"


# ==== Generated test for GET /id/EMP123 ====
# Sure, here is an example of a Pytest function that can be used for the scenario you've described using requests module.


def test_get_employee():
    # Define the URL
    url = f'{BASE_URL}/id/EMP123'
    
    # Make the GET request
    response = requests.get(url)

    # Assert that the status code is 200
    assert response.status_code == 200, "Expected status code 200, but got {response.status_code}"

    # Convert to JSON
    data = response.json()

    # Check that the response has the expected structure
    assert 'id' in data, "Missing 'id' in response"
    assert 'name' in data, "Missing 'name' in response"
    assert 'department' in data, "Missing 'department' in response"
    assert 'title' in data, "Missing 'title' in response"

    # Check that the id value is correct
    assert data['id'] == 'EMP123', f"Expected id to be 'EMP123', but got {data['id']}"

"""
This function sends a GET request to the given URL and checks that the status code is 200 (indicating a successful response). It then converts the response data to JSON and checks that certain expected fields ('id', 'name', 'department', 'title') are present. Finally it checks that the value for 'id' matches the expected value ('EMP123'). If any of these checks fail, pytest will report the test as failed.

# ==== Generated test for GET /search ====
Here's a Pytest function that tests getting information from the 'http://localhost:8080/employees/search' endpoint with specific parameters:
"""


search_url = 'http://localhost:8080/employees/search'
params = {'department': 'Engineering', 'position': 'Manager', 'salary': 50000}

def test_get_request():
    response = requests.get(search_url, params=params)
    data = response.json()

    # Basic status code assertion
    assert response.status_code == 200, f'Expected status code 200, but received {response.status_code} instead.'

    # Basic response structure assertions
    assert 'employees' in data, 'The key "employees" is missing from the response data'
    assert isinstance(data['employees'], list), 'The "employees" data is not in list type'

    for employee in data['employees']:
        assert 'id' in employee, 'The key "id" is missing for an employee in the response data'
        assert 'name' in employee, 'The key "name" is missing for an employee in the response data'
        assert 'position' in employee, 'The key "position" is missing for an employee in the response data'
        assert 'salary' in employee, 'The key "salary" is missing for an employee in the response data'
        assert 'department' in employee, 'The key "department" is missing for an employee in the response data'

        # Asserting that returned data matches search parameters
        assert employee['department'] == 'Engineering', "Employee's department does not match search parameter"
        assert employee['position'] == 'Manager', "Employee's position does not match search parameter"
        assert employee['salary'] >= 50000, "Employee's salary does not meet or exceed search parameter"

#This function sends a GET request to the specified URL with the given parameters. It then checks that it receives a 200 status code (indicating a successful request) and that the response data contains the keys 'employees', 'id', 'name', 'position', 'salary', and 'department'.
#This function also checks that the 'employees' key maps to a list and that each item in this list has the keys 'id', 'name', 'position', 'salary', and 'department'. It then validates if the returned data for each employee matches the search parameters.

# ==== Generated test for GET /download ====
#Sure, here is a pytest test function for the specified GET request:


def test_get_employees_download():
    url = f"{BASE_URL}/download"
    querystring = {'department': 'Engineering', 'position': 'Manager', 'salary': '50000'}
    response = requests.get(url, params=querystring)

    # Assertion for HTTP status code
    assert response.status_code == 200, f"Expected status code 200, but got {response.status_code}"

    response_content = response.json()

    # Assertions for basic response structure
    assert "employees" in response_content, f"Expected 'employees' in response, but got {response_content.keys()}"
    
    for employee in response_content["employees"]:
        assert "id" in employee, f"Expected 'id' in each employee, but got {employee.keys()}"
        assert "name" in employee, f"Expected 'name' in each employee, but got {employee.keys()}"
        assert "department" in employee, f"Expected 'department' in each employee, but got {employee.keys()}"
        assert "position" in employee, f"Expected 'position' in each employee, but got {employee.keys()}"
        assert "salary" in employee, f"Expected 'salary' in each employee, but got {employee.keys()}"

        assert employee["department"] == "Engineering", f"Expected department to be 'Engineering', but got {employee['department']}"
        assert employee["position"] == "Manager", f"Expected position to be 'Manager', but got {employee['position']}"
        assert int(employee["salary"]) >= 50000, f"Expected salary to be more than or equal to 50000, but got {employee['salary']}"
# This test function sends a GET request to the specified URL and checks the status code of the response. It then parses the content of the response to JSON and checks the existence of expected keys in the response. It also validates if the values for 'department', 'position' and 'salary' are as expected.

# ==== Generated test for PUT /id/EMP123 ====

url = f'{BASE_URL}/id/EMP123'
headers = {'content-type': 'application/json'}
body = {'firstName': 'John', 'lastName': 'Doe', 'email': 'john.doe@example.com'}

def test_put_employee():
    response = requests.put(url, data=json.dumps(body), headers=headers)
    response_body = response.json()

    # Assert that the response code is 200 (HTTP OK)
    assert response.status_code == 200

    # Assert that the response structure contains expected fields
    assert 'firstName' in response_body
    assert 'lastName' in response_body
    assert 'email' in response_body

    # Assert that the response data is correct
    assert response_body['firstName'] == body['firstName']
    assert response_body['lastName'] == body['lastName']
    assert response_body['email'] == body['email']
#This script will make a PUT request to update the employee data and then certain assertions will check if the request was successful and the returned data is correct. It checks if the status code is 200 (OK) and the response structure contains the required fields. It also checks if the employee data returned in the response matches with the updated data.

#Make sure you replace the URL, and request body data with actual ones when running this test. Please note that the design of the test can largely depend on the specific implementation of the API endpoint. Therefore, the provided script might need some adjustments to run correctly.

# ==== Generated test for POST  ====
# Sure, Here is a basic Pytest function for testing the POST endpoint at '/employees'. This function uses the `requests` library to send a POST request, and then it checks that the status code of the HTTP response is 201 (Created) and the response structure includes 'id', 'firstName', 'lastName' and 'email'.


def test_post_employee():
    
    # Define the api endpoint
    url = 'http://localhost:8080/employees'
    
    # Define the payload for the post request
    payload = {
               'firstName': 'Alice', 
               'lastName': 'Smith', 
               'email': 'alice.smith@example.com'
            }
    
    # Make a post request to the api
    response = requests.post(url, json=payload)
    
    # Parse response JSON
    response_json = response.json()

    # Assert that the response code is 201
    assert response.status_code == 201

    # Assert that the response structure is correct
    assert 'id' in response_json
    assert 'firstName' in response_json
    assert response_json['firstName'] == 'Alice'
    assert 'lastName' in response_json
    assert response_json['lastName'] == 'Smith'
    assert 'email' in response_json
    assert response_json['email'] == 'alice.smith@example.com'

# This test function `test_post_employee` will fail if the POST request doesn't return a HTTP 201 status code or if the response structure is not as expected.

# ==== Generated test for DELETE /id/EMP123 ====
# Sure, Here is a simple Pytest test function for a DELETE request in Python using the requests library:


def test_delete_employee_by_id():
    # Define the base URL
    base_url = 'http://localhost:8080/employees/id/'

    # Define the employee ID
    employee_id = 'EMP123'

    # Make the DELETE request
    response = requests.delete(f"{base_url}{employee_id}")

    # Assert that the status code is 200 (OK)
    assert response.status_code == 200, f"Expected status code 200, but got {response.status_code}"

    # Get the JSON response body
    response_body = response.json()

    # Assert basic response body structure (assuming that response is a dictionary containing 'message' key)
    assert "message" in response_body, "The response body does not contain 'message' key"

    # Assert 'message' content
    assert response_body['message'] == f"Employee {employee_id} deleted successfully", f"Expected message 'Employee {employee_id} deleted successfully', but got {response_body['message']}"

# This test makes a DELETE request to the 'http://localhost:8080/employees/id/EMP123' endpoint and asserts that the HTTP status code is 200(OK) and that the response body contains the 'message' key with proper content. Please adjust the expectations according to your API's actual behavior.

