import psycopg2
from psycopg2.extras import DictCursor
import os


def _get_connection():

    # Establish connection
    conn = psycopg2.connect(
        dbname="employeeDB",
        user=os.getenv("POSTGRESQL_USERNAME"),
        password=os.getenv("POSTGRESQL_PASSWORD"),
        host="localhost",
        port="5432"  # default PostgreSQL port
    )

    # Create a cursor and return it
    conn.autocommit = True
    return conn.cursor(cursor_factory=DictCursor)


def get_employee_from_db(employee_id):
     with _get_connection() as cursor:
         cursor.execute("SELECT first_name as firstName, last_name as lastName, email FROM employees WHERE employee_id = %s", (employee_id,))
         employee = cursor.fetchone()
     return {"firstName": employee[0], "lastName": employee[1], "email": employee[2]} if employee else None

def get_all_employees_from_db():
    """
    Fetch all employees from the database.

    Returns:
        list[dict]: A list of dictionaries containing employee details
        (firstName, lastName, email). Returns an empty list if no employees are found.
    """
    with _get_connection() as cursor:
        cursor.execute("SELECT first_name as firstName, last_name as lastName, email FROM employees")
        employees = cursor.fetchall()
    return [{"firstName": first, "lastName": last, "email": email} for first, last, email in employees]

def get_all_emp_ids_from_db():
    """
    Fetch all ids from the database.

    Returns:
        list[dict]: A list of ids
         Returns an empty list if no ids are found.
    """
    with _get_connection() as cursor:
        cursor.execute("SELECT employee_id as ID FROM employees")
        employees = cursor.fetchall()

    return [employee[0] for employee in employees]

if __name__ == "__main__":
    print(get_all_emp_ids_from_db())