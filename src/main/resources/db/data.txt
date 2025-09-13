-- Drop tables if they exist
DROP TABLE IF EXISTS authorities CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS system_users CASCADE;
DROP TABLE IF EXISTS employee CASCADE;

-- Create system_users
CREATE TABLE system_users (
    user_id VARCHAR(50) PRIMARY KEY,
    password VARCHAR(68) NOT NULL,
    active BOOLEAN NOT NULL
);

-- fun123

INSERT INTO system_users (user_id, password, active)
VALUES
    ('john', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true),
    ('mary', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true),
    ('susan', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true),
    ('bright', '{bcrypt}$2a$10$6Z2Qf7pLFNOvMQEMQlLv/OEEqDWgkRxXAxlak.AZCO6HA0HeLhMfm', true);

-- Create roles
CREATE TABLE roles (
    user_id VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL,
    UNIQUE (user_id, role),
    FOREIGN KEY (user_id) REFERENCES system_users (user_id)
);

INSERT INTO roles (user_id, role)
VALUES
    ('john', 'ROLE_EMPLOYEE'),
    ('mary', 'ROLE_EMPLOYEE'),
    ('mary', 'ROLE_MANAGER'),
    ('susan', 'ROLE_EMPLOYEE'),
    ('susan', 'ROLE_MANAGER'),
    ('susan', 'ROLE_ADMIN'),
    ('bright', 'ROLE_EMPLOYEE'),
    ('bright', 'ROLE_MANAGER'),
    ('bright', 'ROLE_ADMIN');

-- Create employee table
CREATE TABLE employee (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(45),
    last_name VARCHAR(45),
    email VARCHAR(45) UNIQUE
);

-- Insert data
INSERT INTO employee (first_name, last_name, email)
VALUES
    ('Leslie', 'Andrews', 'leslie@luv2code.com'),
    ('Emma', 'Baumgarten', 'emma@luv2code.com'),
    ('Emeka', 'Wright', 'emeka@luv2code.com'),
    ('John', 'Peterson', 'john@luv2code.com'),
    ('Peter', 'Simmons', 'peter@luv2code.com'),
    ('James', 'Gartner', 'james@luv2code.com'),
    ('Yuri', 'Petrov', 'yuri@luv2code.com'),
    ('Nicholas', 'Hogan', 'nicholas.hogan@hotmail.com'),
    ('Megan', 'Walker', 'megan.walker@yahoo.com'),
    ('Larry', 'Simon', 'larry.simon@hotmail.com'),
    ('Angela', 'Ryan', 'angela.ryan@yahoo.com'),
    ('Christopher', 'Richardson', 'christopher.richardson@outlook.com'),
    ('Nicole', 'Moore', 'nicole.moore@gmail.com'),
    ('Joseph', 'Harrison', 'joseph.harrison@yahoo.com'),
    ('Autumn', 'Luna', 'autumn.luna@outlook.com'),
    ('Lisa', 'Murphy', 'lisa.murphy@hotmail.com'),
    ('Cory', 'Mckinney', 'cory.mckinney@outlook.com'),
    ('John', 'Gomez', 'john.gomez@hotmail.com'),
    ('John', 'Murphy', 'john.murphy@outlook.com'),
    ('Jessica', 'Martinez', 'jessica.martinez@hotmail.com'),
    ('Jeffrey', 'Lewis', 'jeffrey.lewis@hotmail.com'),
    ('Nicholas', 'Parks', 'nicholas.parks@gmail.com'),
    ('Alexander', 'Brewer', 'alexander.brewer@hotmail.com'),
    ('Jared', 'Fowler', 'jared.fowler@outlook.com'),
    ('Sophia', 'Pacheco', 'sophia.pacheco@hotmail.com'),
    ('Christopher', 'Spencer', 'christopher.spencer@hotmail.com'),
    ('Savannah', 'Cook', 'savannah.cook@outlook.com'),
    ('Alexander', 'Oneal', 'alexander.oneal@hotmail.com'),
    ('Gloria', 'Thomas', 'gloria.thomas@yahoo.com'),
    ('Donna', 'Walker', 'donna.walker@gmail.com'),
    ('Ryan', 'Gordon', 'ryan.gordon@outlook.com'),
    ('Randy', 'Watson', 'randy.watson@outlook.com'),
    ('Scott', 'Becker', 'scott.becker@yahoo.com'),
    ('Nicholas', 'Navarro', 'nicholas.navarro@gmail.com'),
    ('Jim', 'Reese', 'jim.reese@yahoo.com'),
    ('Amanda', 'Griffin', 'amanda.griffin@outlook.com'),
    ('Allison', 'Hayes', 'allison.hayes@hotmail.com'),
    ('Catherine', 'Rivera', 'catherine.rivera@gmail.com'),
    ('Brad', 'Gordon', 'brad.gordon@outlook.com'),
    ('Tina', 'Gomez', 'tina.gomez@yahoo.com'),
    ('Timothy', 'Mathis', 'timothy.mathis@yahoo.com'),
    ('Brandy', 'Thomas', 'brandy.thomas@outlook.com'),
    ('Brian', 'Andrews', 'brian.andrews@yahoo.com'),
    ('Jeffrey', 'Kennedy', 'jeffrey.kennedy@gmail.com'),
    ('Troy', 'Noble', 'troy.noble@hotmail.com'),
    ('Paul', 'Jones', 'paul.jones@hotmail.com'),
    ('Jessica', 'Walker', 'jessica.walker@yahoo.com'),
    ('Juan', 'Vega', 'juan@luv2code.com'),
    ('Sean', 'Wiggins', 'sean.wiggins@yahoo.com'),
    ('Mary', 'West', 'mary.west@outlook.com'),
    ('Vicki', 'Ford', 'vicki.ford@gmail.com'),
    ('Cheryl', 'Andrews', 'cheryl.andrews@yahoo.com'),
    ('Rebecca', 'Mitchell', 'rebecca.mitchell@yahoo.com'),
    ('Karen', 'Roberts', 'karen.roberts@gmail.com'),
    ('David', 'Jenkins', 'david.jenkins@hotmail.com'),
    ('Amy', 'Curtis', 'amy.curtis@outlook.com'),
    ('Vincent', 'Beard', 'vincent.beard@hotmail.com'),
    ('Gabriela', 'Wells', 'gabriela.wells@yahoo.com'),
    ('Craig', 'Bowen', 'craig.bowen@gmail.com'),
    ('Julia', 'Wright', 'julia.wright@outlook.com'),
    ('Amy', 'Benton', 'amy.benton@outlook.com'),
    ('George', 'Wu', 'george.wu@gmail.com'),
    ('Elizabeth', 'Krueger', 'elizabeth.krueger@hotmail.com'),
    ('Nicole', 'Jennings', 'nicole.jennings@hotmail.com'),
    ('Timothy', 'Powell', 'timothy.powell@hotmail.com'),
    ('Tara', 'Kim', 'tara.kim@gmail.com'),
    ('James', 'Smith', 'james.smith@outlook.com'),
    ('Jasmine', 'Williams', 'jasmine.williams@yahoo.com'),
    ('Patricia', 'Clark', 'patricia.clark@yahoo.com'),
    ('Nicole', 'Johnson', 'nicole.johnson@hotmail.com'),
    ('Crystal', 'Bowers', 'crystal.bowers@hotmail.com'),
    ('Tracy', 'Graham', 'tracy.graham@yahoo.com'),
    ('Edward', 'Aguilar', 'edward.aguilar@hotmail.com'),
    ('Sharon', 'James', 'sharon.james@outlook.com'),
    ('Mary', 'Dodson', 'mary.dodson@gmail.com'),
    ('Linda', 'Morton', 'linda.morton@outlook.com'),
    ('Bobby', 'Miller', 'bobby.miller@hotmail.com'),
    ('Jennifer', 'Williams', 'jennifer.williams@hotmail.com'),
    ('Bruce', 'Thompson', 'bruce.thompson@yahoo.com'),
    ('Tony', 'Salazar', 'tony.salazar@outlook.com'),
    ('Mark', 'Miller', 'mark.miller@outlook.com'),
    ('Robert', 'Flores', 'robert.flores@outlook.com'),
    ('Patricia', 'Mendez', 'patricia.mendez@outlook.com'),
    ('Christopher', 'Collins', 'christopher.collins@hotmail.com'),
    ('Jesus', 'Butler', 'jesus.butler@gmail.com'),
    ('Heather', 'Jacobs', 'heather.jacobs@hotmail.com'),
    ('Elijah', 'Jacobs', 'elijah.jacobs@hotmail.com'),
    ('Loretta', 'Jackson', 'loretta.jackson@outlook.com'),
    ('Christopher', 'Moore', 'christopher.moore@hotmail.com'),
    ('Pamela', 'Schwartz', 'pamela.schwartz@outlook.com'),
    ('Kim', 'Rivera', 'kim.rivera@gmail.com'),
    ('Anna', 'Lee', 'anna.lee@outlook.com'),
    ('Michael', 'Moore', 'michael.moore@yahoo.com'),
    ('Reginald', 'Casey', 'reginald.casey@gmail.com'),
    ('Suzanne', 'Larsen', 'suzanne.larsen@outlook.com'),
    ('Shannon', 'Morris', 'shannon.morris@hotmail.com'),
    ('Mary', 'Pham', 'mary.pham@gmail.com'),
    ('Aaron', 'White', 'aaron.white@gmail.com'),
    ('Katrina', 'Valenzuela', 'katrina.valenzuela@gmail.com'),
    ('Ashley', 'Olson', 'ashley.olson@hotmail.com'),
    ('Michelle', 'Dunn', 'michelle.dunn@hotmail.com'),
    ('William', 'Ford', 'william.ford@outlook.com'),
    ('Julie', 'Taylor', 'julie.taylor@hotmail.com'),
    ('Mary', 'Savage', 'mary.savage@yahoo.com'),
    ('Thomas', 'Rivas', 'thomas.rivas@gmail.com'),
    ('Evan', 'Palmer', 'evan.palmer@hotmail.com'),
    ('Beverly', 'Crawford', 'beverly.crawford@gmail.com'),
    ('Alexander', 'Shelton', 'alexander.shelton@gmail.com'),
    ('Omar', 'Brown', 'omar.brown@outlook.com'),
    ('Amber', 'Morgan', 'amber.morgan@outlook.com'),
    ('Tracy', 'Bell', 'tracy.bell@hotmail.com'),
    ('Robert', 'Perkins', 'robert.perkins@yahoo.com'),
    ('Tammy', 'Wells', 'tammy.wells@gmail.com'),
    ('Ryan', 'Mercer', 'ryan.mercer@gmail.com'),
    ('Nicholas', 'Johnson', 'nicholas.johnson@hotmail.com'),
    ('Jeremy', 'Krueger', 'jeremy.krueger@gmail.com'),
    ('Daniel', 'Webb', 'daniel.webb@outlook.com'),
    ('John', 'Moon', 'john.moon@yahoo.com'),
    ('Antonio', 'Long', 'antonio.long@yahoo.com'),
    ('Sonya', 'Snyder', 'sonya.snyder@gmail.com'),
    ('Deanna', 'Johnson', 'deanna.johnson@yahoo.com'),
    ('Brian', 'Obrien', 'brian.obrien@outlook.com'),
    ('Katherine', 'Martinez', 'katherine.martinez@outlook.com'),
    ('Ricky', 'Hatfield', 'ricky.hatfield@gmail.com'),
    ('Randall', 'Barrera', 'randall.barrera@outlook.com'),
    ('Daniel', 'Rhodes', 'daniel.rhodes@yahoo.com'),
    ('Jeffrey', 'Norris', 'jeffrey.norris@outlook.com'),
    ('Melissa', 'Brown', 'melissa.brown@gmail.com');
