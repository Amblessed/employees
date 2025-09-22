import subprocess
import pytest
import os, shutil, sys
import socket
import platform
import time
import requests
from urllib3.util.retry import Retry
from requests.adapters import HTTPAdapter
from pathlib import Path
import signal

# --- Configuration ---
MVN_EXEC = "mvn.cmd" if platform.system() == "Windows" else "mvn"
MAVEN_HOME = os.environ.get("MVN_HOME", r"C:\apache-maven-3.9.5") # default if not set
MAVEN_CMD = os.path.join(MAVEN_HOME, "bin", MVN_EXEC)
SPRING_BOOT_CMD = [MAVEN_CMD, "spring-boot:run"]


# Server config
SERVER_PORT = 9090
BASE_URL = f"http://localhost:{SERVER_PORT}/api/employees/"

# Timing constants
STARTUP_WAIT = 90       # seconds to wait for app startup
POLL_INTERVAL = 1       # polling interval in seconds
SLEEP_TIME = 300        # cooldown time in seconds
MAX_RETRIES = 10        # number of attempts
WAIT_SECONDS = 3        # seconds between retries

# Project paths
PROJECT_ROOT = Path(__file__).resolve().parents[3]

RESULTS_DIR = PROJECT_ROOT / "allure-results"
USER_DETAILS_PATH = Path("src/test/resources/user_details.json")
USER_DETAILS_FILE = PROJECT_ROOT / "src/test/resources/user_details.json"


def is_port_open(port, host="localhost"):
    """Check if a TCP port is open on a host."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.settimeout(0.5)  # quick timeout to avoid blocking
        try:
            sock.connect((host, port))
            return True
        except (ConnectionRefusedError, socket.timeout):
            return False

def wait_for_port(port, host="localhost", timeout=60, interval=0.5):
    """
    Wait until a TCP port is open or until timeout.

    Args:
        port (int): Port number to check.
        host (str): Hostname or IP.
        timeout (float): Maximum seconds to wait.
        interval (float): Seconds to wait between checks.

    Returns:
        True if port opens within timeout.

    Raises:
        TimeoutError: If port is not open within timeout.
    """
    start = time.time()
    while True:
        if is_port_open(port, host):
            print(f"Port {port} is open on {host}")
            return True
        elapsed = time.time() - start
        if elapsed >= timeout:
            raise TimeoutError(f"Server not available on {host}:{port} after {timeout}s")
        time.sleep(min(interval, timeout - elapsed))


def free_port(port: int):
    """Kill any process using the given port (cross-platform)."""
    if os.name == "nt":  # Windows
        cmd = f'netstat -ano | findstr :{port}'
        output = subprocess.getoutput(cmd)
        for line in output.splitlines():
            parts = line.strip().split()
            if len(parts) >= 5:
                pid = parts[-1]
                subprocess.run(["taskkill", "/F", "/PID", pid])
    else:  # Unix/Linux/macOS
        cmd = f"lsof -ti:{port}"
        output = subprocess.getoutput(cmd)
        for pid in output.splitlines():
            os.kill(int(pid), signal.SIGKILL)


@pytest.fixture(scope="session", autouse=True)
def spring_boot_server():

    """
    Start Spring Boot only if running locally.
    On CI (GitHub Actions), the server is started by the workflow.
    """
    if os.getenv("CI", "false").lower() == "true":
        print("CI environment detected — assuming Spring Boot is already running.")
        yield
        return

    print("Freeing port 9090 if in use...")
    free_port(SERVER_PORT)
    print(f"Project root: {PROJECT_ROOT}")

    # Start Spring Boot
    print("\nStarting Spring Boot server...")
    # log_file = "springboot" + "_" + time.strftime("%Y%m%d_%H%M%S") + ".log"
    log_file = "springboot.log"
    with open(log_file, "w") as log:
        process = subprocess.Popen(SPRING_BOOT_CMD, cwd=PROJECT_ROOT, stdout=log, stderr=log)


    # Wait until server is up
    print(f"Waiting for Spring Boot to start on port {SERVER_PORT}...")
    wait_for_port(SERVER_PORT, timeout=STARTUP_WAIT)
    wait_for_server()
    # Wait dynamically for seeder JSON
    wait_for_seeder_json(timeout=SLEEP_TIME)


    # Run tests
    yield

    # Teardown: stop Spring Boot
    print("\nStopping Spring Boot server...")
    if os.name == 'nt':  # Windows
        subprocess.run(["taskkill", "/F", "/T", "/PID", str(process.pid)])
    else:  # Unix/Linux/Mac
        process.terminate()
    process.wait()


def run_command(cmd, description: str):
    """Run a subprocess command and print stdout/stderr."""
    print(description)
    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.stdout:
        print("STDOUT: ", result.stdout)
    if result.stderr:
        print("STDERR: ", result.stderr)
    return result.returncode

def pytest_sessionstart(session):
    """Clean allure-results and create environment.properties before tests start."""

    # Clean previous results
    if os.path.exists(RESULTS_DIR):
        print("Cleaning old Allure results...")
        shutil.rmtree(RESULTS_DIR)
    os.makedirs(RESULTS_DIR, exist_ok=True)

    # Create environment.properties
    env_file = os.path.join(RESULTS_DIR, "environment.properties")
    os_name = f"{platform.system()} {platform.release()}"
    python_version = f"{sys.version_info.major}.{sys.version_info.minor}.{sys.version_info.micro}"
    with open(env_file, "w") as f:
        f.write(f"OS={os_name}\n")
        f.write(f"Python={python_version}\n")
        f.write("Project=EmployeesApp\n")
        f.write(f"API_BASE_URL={BASE_URL}\n")

def pytest_sessionfinish(session, exitstatus):
    """After tests finish, generate and open Allure report automatically."""

    if os.getenv("CI", "false").lower() == "true":
        return

    report_dir = os.path.join(PROJECT_ROOT, "allure-report")
    allure_cmd = r"C:\allure\allure-2.35.1\bin\allure.bat"

    # Generate report
    generate_cmd = [allure_cmd, "generate", RESULTS_DIR, "--clean", "-o", report_dir]
    if run_command(generate_cmd, description="Generating Allure report...") != 0:
        print("Allure report generation failed!")
        return

    # Open report
    print("Opening Allure report in browser...")
    subprocess.Popen([allure_cmd, "open", report_dir])

def wait_for_server(url=BASE_URL):
    session = requests.Session()
    retries = Retry(total=MAX_RETRIES, backoff_factor=1, status_forcelist=[500, 502, 503, 504])
    session.mount('http://', HTTPAdapter(max_retries=retries))

    for attempt in range(MAX_RETRIES):
        try:
            resp = session.get(url.replace("/api/employees/", "/actuator/health"), timeout=5)
            if resp.status_code == 200:
                print(f"Server is up! ({attempt + 1}/{MAX_RETRIES})")
                return True
        except requests.exceptions.RequestException:
            pass
        print(f"Waiting for server... ({attempt + 1}/{MAX_RETRIES})")
        time.sleep(WAIT_SECONDS)
    raise RuntimeError("Server did not become ready in time")


def wait_for_seeder_json(timeout=60):
    """Wait until a fresh user_details.json is created by the Spring Boot seeder."""
    START_TIME = time.time()
    print("⏳ Waiting for fresh user_details.json to be generated...")
    for _ in range(timeout):
        if USER_DETAILS_FILE.exists() and USER_DETAILS_FILE.stat().st_size > 0:
            modified = USER_DETAILS_FILE.stat().st_mtime
            if modified > START_TIME:
                print("✅ Fresh Seeder JSON found!")
                return
        time.sleep(POLL_INTERVAL)
    raise FileNotFoundError(f"{USER_DETAILS_FILE} not freshly updated after {timeout} seconds")
