Group \# 2   
Group Members:

- Barbin, Kurt Angelo  
- Cayabyab, Ace Justine  
- Inandan, Derick  
- Lingao, Julianne Therese

Section: C2B

1. Introduction  
   1. Project Overview:  
      1. Briefly describe the system you plan to create. Explain what problem it addresses or what purpose it serves.  
- The Pet Adoption System is designed to make the pet adoption process easier, more organized, and more convenient for both users and admins. Through the system, users can browse pets that are available for adoption, search and filter them based on their preferences, and view a Pet Passport that contains important information about each pet, such as its name, type, breed, age, gender, color, weight, health condition, vaccination status, description, and current adoption status.  
- If a user is interested in adopting a pet, they can submit an adoption application through the system and provide the necessary information needed for the application. They can also view the status of their application, update it while it is still pending, cancel it if needed, and view their adoption history once an adoption has been completed.  
- On the admin side, the system allows admins to manage pet records and user accounts, review adoption applications, approve or reject applications, update pet and application statuses, and maintain records of completed adoptions. By keeping these processes in one system, important information becomes easier to organize, access, and monitor.  
  2. Objective:  
     1. State the main goals of your project, including what you aim to achieve by implementing this system.  
- The main objective of the Pet Adoption System is to provide a simple and organized platform where people can easily find pets that are available for adoption and apply to adopt them.  
- The system aims to help users make more informed decisions before adopting by providing detailed information about each pet through the Pet Passport, including its basic information, health condition, vaccination status, and adoption status. Users can also search and filter available pets based on their preferences, making it easier to find a pet that may be suitable for them.  
- The system also aims to make the adoption process easier to manage by allowing users to submit and monitor their applications in one place. At the same time, admins can properly manage pet information, review adoption applications, update their status, manage user accounts, and keep records of successful adoptions.  
- Overall, the Pet Adoption System aims to create a more convenient and organized adoption process while helping prospective adopters learn more about the pets before making the decision to adopt.


1. System Features and Functionalities  
   1. Core Functionalities:

## Regular User Features

| Feature | Description | Operations |
| ----- | ----- | ----- |
| Register Account | Allows a new user to create an account in the system by providing required personal information such as full name, email address, contact number, address, username, and password. The system validates required fields, checks for duplicate usernames or email addresses, and ensures that passwords meet the required format before creating the account. | Create |
| Login / Logout | Allows registered users to securely access the system using their username or email and password. The system verifies the entered credentials before granting access. Users can also log out to safely end their current session and prevent unauthorized access to their account. | Additional Operation |
| Manage Profile | Allows users to view and update their personal information such as name, email address, contact number, address, and password. The system validates updated information before saving any changes. Users can only manage their own profile and cannot change their assigned role. | Read, Update |
| Browse Pets | Allows users to view all pets that are currently listed in the system. The pet list may display basic information such as pet name, type, breed, age, gender, and current adoption status. Users can select a pet to view its complete information. | Read |
| View Pet Passport | Allows users to view the complete information of a selected pet. The Pet Passport may contain the pet’s name, type, breed, age, gender, color, weight, health condition, vaccination status, description, and current adoption status. This helps users learn more about a pet before deciding to apply for adoption. | Read |
| Search and Filter Pets | Allows users to quickly find specific pets by entering a pet name or keyword. Users can also filter pets based on type, breed, age, gender, or adoption status. Multiple filters may be used to make searching easier and more specific. | Additional Operation |
| Submit Adoption Application | Allows a logged-in user to submit an adoption application for a pet that is currently available. The application may include the user’s contact information, address, occupation, living situation, previous pet ownership experience, and reason for adoption. The system validates all required fields and prevents duplicate active applications for the same pet. New applications are automatically marked as Pending. | Create |
| View Adoption Applications | Allows users to view all adoption applications they have submitted. Each application may display the pet being applied for, application date, application details, and current status such as Pending, Approved, Rejected, or Cancelled. Users can only view their own applications. | Read |
| Update Pending Application | Allows users to edit information in their adoption application while its status is still Pending. Users may update details such as contact information, address, occupation, living situation, or reason for adoption. Approved, Rejected, or Cancelled applications can no longer be edited. | Update |
| Cancel Adoption Application | Allows users to cancel an adoption application that they no longer want to continue. Only applications that are still Pending can be cancelled. The system may require the user to confirm the cancellation before processing it. Cancelled applications can no longer be approved or edited. | Delete |
| View Adoption History | Allows users to view records of pets they have successfully adopted. The history may include the pet’s name, type, breed, adoption date, and related application information. Only completed and approved adoptions are included in the user’s adoption history. | Read |

## Admin Features

| Feature | Description | Operations |
| ----- | ----- | ----- |
| Admin Login / Logout | Allows admins to securely access the admin side of the system using valid admin credentials. The system verifies both the login credentials and the assigned role before allowing access to admin functions. Admins can also log out to securely end their session. | Additional Operation |
| Manage Pets | Allows admins to add new pets, view existing pet records, update pet information, and remove pet records when necessary. Pet information may include name, type, breed, age, gender, color, weight, health information, vaccination status, description, and adoption status. The system validates required fields and prevents invalid values such as negative ages or weights. | Create, Read, Update, Delete |
| Update Pet Status | Allows admins to change a pet’s adoption status depending on its current situation. Available statuses may include Available, Pending Adoption, and Adopted. For example, a pet may change from Available to Pending Adoption once an application is approved, and later to Adopted when the adoption process is completed. | Update |
| Manage Users | Allows admins to create user accounts, view registered users, update user information, disable accounts, or remove user accounts when necessary. Admins may manage information such as name, email, contact number, address, username, and account status. Users with existing adoption records may be disabled instead of permanently deleted to preserve historical data. | Create, Read, Update, Delete |
| View Adoption Applications | Allows admins to view all adoption applications submitted by Regular Users. The Admin can review information such as the applicant’s name, selected pet, submission date, reason for adoption, living situation, previous pet experience, and current application status. | Read |
| Update Application Status | Allows admins to change the status of an adoption application based on the result of the review. Possible statuses include Pending, Approved, Rejected, or Cancelled. Only valid status changes should be allowed by the system to maintain correct application records. | Update |
| Approve Adoption Application | Allows admins to approve an adoption request when the applicant meets the required conditions. Once approved, the application’s status becomes Approved and the selected pet may be changed to Pending Adoption. The system must ensure that the application is still Pending and that the pet has not already been adopted. | Additional Operation |
| Reject Adoption Application | Allows admins to reject applications that do not meet the adoption requirements or cannot continue. The Admin may provide a reason for rejection, such as incomplete information, duplicate application, failure to meet requirements, or the pet already having another approved adopter. | Additional Operation |
| Delete Invalid Applications | Allows admins to remove applications that are considered invalid, duplicated, incorrectly created, or unnecessary. The system should request confirmation before deletion. Approved or completed applications should normally be preserved because they may be connected to adoption history records. | Delete |
| Manage Adoption Records | Allows admins to create, view, update, and remove completed adoption records. An adoption record may contain the adoption record ID, adopter information, pet information, related application, and adoption date. These records provide official documentation of successful adoptions completed through the system. | Create, Read, Update, Delete |
| View Adoption History | Allows admins to view the complete history of successful adoptions made through the system. The Admin may view details such as the adopter’s name, adopted pet, application information, and adoption date. This gives the Admin a complete record of past adoption transactions. | Read |
| Search and Filter Records | Allows admins to search and filter different types of records within the system. Admins can search pets by name or breed, users by name or email, applications by applicant or pet, and adoption records by adopter or pet. Records may also be filtered by status, type, gender, date, or other available categories. | Additional Operation |
| View Adoption Statistics  | Allows admins to view adoption statistics by selected periods such as monthly or yearly, including the number of applications received and successful adoptions.  | Read  |

2. **Roles**  
   1. **Regular User**  
       A Regular User is a person who wants to browse pets and submit an adoption application. They can register and log in, manage their profile, view available pets, search and filter pets, view pet details, submit an adoption application, update or cancel a pending application, check the status of their application, and view their adoption history.  
   2. **Admin**  
       An Admin is responsible for managing the entire system. They can log in to the admin side, manage pet records through CRUD operations, manage user accounts, review adoption applications, approve or reject applications, update pet and application statuses, manage completed adoption records, search and filter records, and view the overall adoption history.

# **Pet Adoption System**

## **System Overview**

The **Pet Adoption System** is a Java-based application designed to help users browse pets that are available for adoption and submit adoption applications. The system also provides administrators with tools for managing pets, user accounts, adoption applications, and completed adoption records.

The system has two main user roles:

1. **Regular User**  
2. **Admin**

Each role has different permissions and responsibilities. Regular Users mainly interact with the adoption side of the system, while Admins manage and control the information stored in the system.

The system will include:

* User authentication  
* Role-based access  
* CRUD operations  
* Pet management  
* Adoption application management  
* Adoption history  
* Search and filtering  
* Input validation  
* Exception handling  
* Status management

---

# **1\. Regular User Features**

## **1.1 Register Account**

**Description:**  
 Allows a new user to create an account before accessing the main features of the system.

The user must provide valid personal and account information.

### **Possible Information Required**

* First Name  
* Last Name  
* Email Address  
* Contact Number  
* Address  
* Username  
* Password  
* Confirm Password

### **Validations**

* First name must not be empty.  
* Last name must not be empty.  
* Email must follow a valid email format.  
* Email must not already be registered.  
* Username must be unique.  
* Username must meet the required length.  
* Password must meet the required minimum length.  
* Password and confirm password must match.  
* Contact number must contain valid numbers.  
* Required fields cannot be left blank.

### **System Behavior**

After successful registration, the account is stored in the system and the user can log in using their credentials.

**Operation:** Create

---

## **1.2 Login**

**Description:**  
 Allows registered users to securely access the system.

The user enters their username or email and password.

### **Validations**

* Username or email must not be empty.  
* Password must not be empty.  
* The account must exist.  
* The password must match the stored account password.  
* Disabled accounts must not be allowed to log in.

### **System Behavior**

If the credentials are correct, the user is redirected to the Regular User side of the system.

If the credentials are incorrect, the system displays an error message.

**Operation:** Additional Operation

---

## **1.3 Logout**

**Description:**  
 Allows the user to securely end their current session.

After logging out, the user must log in again before accessing protected features.

**Operation:** Additional Operation

---

## **1.4 Manage Profile**

**Description:**  
 Allows users to view and update their personal information.

### **Information That Can Be Viewed**

* User ID  
* Full Name  
* Email  
* Contact Number  
* Address  
* Username

### **Information That Can Be Updated**

* First Name  
* Last Name  
* Email  
* Contact Number  
* Address  
* Password

### **Validations**

* Email must remain unique.  
* Contact number must be valid.  
* Required information cannot be empty.  
* New password must meet password requirements.  
* Confirmation password must match the new password.

Users cannot change their assigned system role.

**Operations:** Read, Update

---

## **1.5 Browse Pets**

**Description:**  
 Allows users to view pets that are currently registered in the system and available for adoption.

Each pet may be displayed using a summary card containing:

* Pet Name  
* Pet Type  
* Breed  
* Age  
* Gender  
* Adoption Status

The user can select a pet to view more detailed information.

**Operation:** Read

---

## **1.6 View Pet Passport**

**Description:**  
 The Pet Passport contains the complete profile and important information about a pet.

### **Pet Information**

* Pet ID  
* Pet Name  
* Pet Type  
* Breed  
* Age  
* Gender  
* Color  
* Weight  
* Description  
* Health Information  
* Vaccination Status  
* Adoption Status

### **Possible Adoption Statuses**

* Available  
* Pending Adoption  
* Adopted

Users can only submit an adoption application if the pet is currently marked as **Available**.

**Operation:** Read

---

## **1.7 Search Pets**

**Description:**  
 Allows users to search for pets using a keyword.

For example, a user may search using:

* Pet Name  
* Breed  
* Pet Type

The system displays pets matching the search term.

**Operation:** Additional Operation

---

## **1.8 Filter Pets**

**Description:**  
 Allows users to narrow down the list of pets according to selected conditions.

### **Possible Filters**

* Pet Type  
* Breed  
* Age  
* Gender  
* Adoption Status

Example:

A user may filter for:

* Type: Dog  
* Gender: Female  
* Status: Available

Only pets matching those conditions will be displayed.

**Operation:** Additional Operation

---

## **1.9 Submit Adoption Application**

**Description:**  
 Allows a user to apply for a pet they want to adopt.

The selected pet must currently be available for adoption.

### **Possible Application Information**

* Application ID  
* User ID  
* Pet ID  
* Applicant Name  
* Address  
* Contact Number  
* Occupation  
* Reason for Adoption  
* Previous Pet Ownership Experience  
* Living Situation  
* Application Date  
* Application Status

### **Initial Application Status**

When an application is first submitted, its status is automatically set to:

**Pending**

### **Validations**

* User must be logged in.  
* Pet must exist.  
* Pet must have an Available status.  
* User cannot submit duplicate applications for the same pet.  
* Required application fields cannot be empty.  
* Contact number must be valid.  
* User must provide a reason for adoption.

### **System Behavior**

After submitting the application, it is sent to the Admin for review.

**Operation:** Create

---

## **1.10 View Adoption Applications**

**Description:**  
 Allows users to view all adoption applications they have submitted.

Each application may display:

* Application ID  
* Pet Name  
* Application Date  
* Current Status

### **Application Statuses**

* Pending  
* Approved  
* Rejected  
* Cancelled

Users can open an application to view its complete details.

**Operation:** Read

---

## **1.11 Update Pending Application**

**Description:**  
 Allows users to edit information in an adoption application while the application is still pending.

### **Editable Information**

Examples include:

* Address  
* Contact Number  
* Occupation  
* Reason for Adoption  
* Living Situation

### **Restrictions**

Users cannot edit an application after it has been:

* Approved  
* Rejected  
* Cancelled

This prevents users from changing information after the Admin has completed the review.

**Operation:** Update

---

## **1.12 Cancel Adoption Application**

**Description:**  
 Allows users to cancel an application they no longer want to continue.

### **Conditions**

The application must still be in the **Pending** status.

The user may be asked to confirm the cancellation before it is completed.

After cancellation, the application status may become:

**Cancelled**

Depending on the system implementation, the record may either remain for historical purposes or be removed from the active application list.

**Operation:** Delete / Cancel Operation

---

## **1.13 View Adoption History**

**Description:**  
 Allows users to view pets they have successfully adopted.

### **Adoption History Information**

* Adoption Record ID  
* Pet Name  
* Pet Type  
* Breed  
* Adoption Date  
* Application ID

Only approved and completed adoptions are displayed in the adoption history.

**Operation:** Read

---

# **2\. Admin Features**

## **2.1 Admin Login**

**Description:**  
 Allows administrators to securely access the administrative side of the system.

Admins must provide valid credentials.

### **Validations**

* Username must not be empty.  
* Password must not be empty.  
* Account must exist.  
* Account must have an Admin role.  
* Password must be correct.  
* Disabled accounts cannot log in.

Users with a Regular User account cannot access Admin functions.

**Operation:** Additional Operation

---

## **2.2 Admin Logout**

**Description:**  
 Allows administrators to securely exit the system.

After logging out, administrative features can no longer be accessed until the Admin logs in again.

**Operation:** Additional Operation

---

# **3\. Pet Management**

## **3.1 Add Pet**

**Description:**  
 Allows Admins to register a new pet in the system.

### **Pet Information**

* Pet ID  
* Name  
* Type  
* Breed  
* Age  
* Gender  
* Color  
* Weight  
* Description  
* Health Information  
* Vaccination Status  
* Adoption Status

### **Validations**

* Pet name cannot be empty.  
* Pet type must be selected.  
* Breed cannot be empty.  
* Age must be a valid positive value.  
* Weight cannot be negative.  
* Gender must have a valid selected value.  
* Adoption status must have a valid status.

Newly added pets may automatically receive the status:

**Available**

**Operation:** Create

---

## **3.2 View Pets**

**Description:**  
 Allows the Admin to view all pets registered in the system.

Unlike Regular Users, Admins can view pets regardless of their adoption status.

This includes:

* Available Pets  
* Pending Adoption Pets  
* Adopted Pets

**Operation:** Read

---

## **3.3 Update Pet Information**

**Description:**  
 Allows the Admin to modify the information of an existing pet.

Examples include changing:

* Name  
* Breed  
* Age  
* Weight  
* Health Information  
* Vaccination Status  
* Description

### **Validation**

The pet must exist before it can be updated.

**Operation:** Update

---

## **3.4 Delete Pet**

**Description:**  
 Allows an Admin to remove a pet record.

### **Restrictions**

The system should prevent deletion if the pet has:

* An active adoption application  
* A completed adoption record

Instead, the Admin may update the pet’s status if deleting it would remove important historical information.

A confirmation should be required before deleting a pet.

**Operation:** Delete

---

## **3.5 Update Pet Status**

**Description:**  
 Allows the Admin to change the adoption status of a pet.

### **Available Statuses**

**Available**  
 The pet can accept adoption applications.

**Pending Adoption**  
 The pet currently has an approved or actively processed application.

**Adopted**  
 The adoption process has been completed.

### **Business Rule**

When an adoption application is approved, the corresponding pet may automatically change from:

Available → Pending Adoption

When the adoption is completed:

Pending Adoption → Adopted

**Operation:** Update

---

# **4\. User Management**

## **4.1 Add User Account**

**Description:**  
 Allows an Admin to manually create a new user account.

The same validation rules used during normal registration should also apply.

**Operation:** Create

---

## **4.2 View Users**

**Description:**  
 Allows the Admin to view all registered user accounts.

### **Information Displayed**

* User ID  
* Full Name  
* Username  
* Email  
* Contact Number  
* Role  
* Account Status

**Operation:** Read

---

## **4.3 Update User Account**

**Description:**  
 Allows an Admin to correct or update user account information.

Examples include:

* Name  
* Email  
* Contact Number  
* Address  
* Account Status

**Operation:** Update

---

## **4.4 Disable User Account**

**Description:**  
 Allows an Admin to prevent a user from logging into the system without permanently deleting their information.

Possible account statuses include:

* Active  
* Disabled

A disabled user cannot log in until the Admin reactivates the account.

**Operation:** Update

---

## **4.5 Delete User Account**

**Description:**  
 Allows an Admin to remove a user account from the system.

### **Restrictions**

Users with existing adoption history should preferably not be permanently deleted because their information may be required for completed adoption records.

In those situations, the account should be disabled instead.

**Operation:** Delete

---

# **5\. Adoption Application Management**

## **5.1 View All Adoption Applications**

**Description:**  
 Allows Admins to view applications submitted by all users.

### **Information Displayed**

* Application ID  
* Applicant Name  
* Pet Name  
* Submission Date  
* Application Status

Admins can open an application to review its complete details.

**Operation:** Read

---

## **5.2 Review Adoption Application**

**Description:**  
 Allows the Admin to review the information submitted by an applicant before making a decision.

The Admin may examine:

* Applicant Information  
* Contact Information  
* Living Situation  
* Pet Ownership Experience  
* Reason for Adoption  
* Selected Pet

**Operation:** Read

---

## **5.3 Approve Adoption Application**

**Description:**  
 Allows an Admin to approve an application if the applicant meets the adoption requirements.

### **System Behavior**

When an application is approved:

1. Application status becomes **Approved**.  
2. Pet status may change to **Pending Adoption**.  
3. Other pending applications for the same pet may be reviewed or rejected.  
4. The approved user becomes the selected adopter.

### **Validations**

* Application must exist.  
* Application must currently be Pending.  
* Pet must not already be Adopted.  
* Application cannot be approved twice.

**Operation:** Update / Additional Operation

---

## **5.4 Reject Adoption Application**

**Description:**  
 Allows an Admin to reject an application.

The Admin may optionally provide a reason for rejection.

Example reasons:

* Incomplete information  
* Applicant does not meet requirements  
* Pet already has an approved adopter  
* Duplicate application

After rejection, the application status becomes:

**Rejected**

**Operation:** Update / Additional Operation

---

## **5.5 Delete Invalid Application**

**Description:**  
 Allows the Admin to remove applications that should not remain in the active system.

Examples include:

* Duplicate applications  
* Invalid applications  
* Test records  
* Incorrectly created records

A confirmation should be displayed before permanent deletion.

Completed or approved applications should not normally be deleted because they may be needed for adoption history.

**Operation:** Delete

---

# **6\. Adoption Record Management**

## **6.1 Create Adoption Record**

**Description:**  
 Once an adoption is successfully completed, the Admin creates an official adoption record.

### **Adoption Record Information**

* Adoption Record ID  
* Application ID  
* User ID  
* Pet ID  
* Adoption Date  
* Adoption Status

The adoption record connects the adopter, pet, and approved application.

**Operation:** Create

---

## **6.2 View Adoption Records**

**Description:**  
 Allows Admins to view completed adoption transactions.

The records provide a permanent history of successful adoptions.

**Operation:** Read

---

## **6.3 Update Adoption Record**

**Description:**  
 Allows Admins to correct information in a completed adoption record if necessary.

Only authorized information should be editable.

**Operation:** Update

---

## **6.4 Delete Adoption Record**

**Description:**  
 Allows an Admin to remove an adoption record if it was created incorrectly.

Because adoption records contain important historical information, deletion should require confirmation and should only be performed when necessary.

**Operation:** Delete

---

# **7\. Adoption History**

## **Admin View Adoption History**

**Description:**  
 Allows the Admin to view all completed adoptions in the system.

### **Information Displayed**

* Adoption Record ID  
* User Name  
* Pet Name  
* Application ID  
* Adoption Date

This provides the Admin with a complete history of adoption transactions.

**Operation:** Read

---

# **8\. Search and Filtering**

## **Admin Search Records**

Admins can search for:

* Pets  
* Users  
* Adoption Applications  
* Adoption Records

Examples:

Search user by:

* Name  
* Username  
* Email

Search pet by:

* Name  
* Breed  
* Type

Search application by:

* Applicant Name  
* Pet Name  
* Status

**Operation:** Additional Operation

---

## **Admin Filter Records**

Admins can filter records based on categories.

Examples include:

### **Pet Filters**

* Type  
* Breed  
* Gender  
* Adoption Status

### **User Filters**

* Account Status  
* Role

### **Application Filters**

* Pending  
* Approved  
* Rejected  
* Cancelled

### **Adoption Record Filters**

* Adoption Date  
* Pet Type

**Operation:** Additional Operation

---

# **9\. User Roles**

## **9.1 Regular User**

A **Regular User** is a person who wants to browse pets and possibly adopt one.

The Regular User can:

* Register an account  
* Log in and log out  
* Manage their profile  
* Browse available pets  
* View pet information  
* Search for pets  
* Filter pets  
* Submit adoption applications  
* View submitted applications  
* Update pending applications  
* Cancel pending applications  
* Check application status  
* View their adoption history

Regular Users only have access to their own account information and adoption applications.

They cannot:

* Add pets  
* Modify pet records  
* Delete pets  
* Manage other users  
* Approve applications  
* Reject applications  
* Manage adoption records  
* Access Admin functions

---

## **9.2 Admin**

An **Admin** is responsible for managing the entire Pet Adoption System.

The Admin can:

* Log in and log out  
* Add pets  
* View pets  
* Update pet information  
* Delete pets  
* Update pet adoption status  
* Add user accounts  
* View users  
* Update user accounts  
* Disable user accounts  
* Delete user accounts  
* View all adoption applications  
* Review applications  
* Approve applications  
* Reject applications  
* Delete invalid applications  
* Create adoption records  
* View adoption records  
* Update adoption records  
* Delete adoption records  
* Search records  
* Filter records  
* View the complete adoption history

Admins have more permissions because they are responsible for maintaining the information stored in the system.

---

# **10\. Main Difference Between Regular User and Admin**

The main difference between the two roles is the level of access and responsibility.

A **Regular User** mainly interacts with the system as a potential adopter. Their actions focus on searching for pets, submitting applications, and monitoring their own adoption process.

An **Admin**, on the other hand, manages the entire adoption system. The Admin controls pet records, user accounts, adoption applications, application decisions, and completed adoption records.

The system uses role-based access to make sure that users can only access features that are allowed for their assigned role.

---

# **11\. CRUD Operations Summary**

| System Record | Create | Read | Update | Delete |
| ----- | ----- | ----- | ----- | ----- |
| User Account | User / Admin | User / Admin | User / Admin | Admin |
| Pet | Admin | User / Admin | Admin | Admin |
| Adoption Application | User | User / Admin | User / Admin | User / Admin |
| Adoption Record | Admin | User / Admin | Admin | Admin |

The system therefore satisfies the required **Create, Read, Update, and Delete operations**.

---

# **12\. Important System Validations**

To prevent incorrect or invalid data from being entered into the system, validation should be applied throughout the application.

### **Required Field Validation**

Required fields cannot be empty.

### **Data Type Validation**

Fields must contain the correct data type.

Examples:

* Age must be numeric.  
* Weight must be numeric.  
* Contact number must contain valid numbers.

### **Range Validation**

Numeric values must stay within reasonable limits.

Examples:

* Age cannot be negative.  
* Weight cannot be negative.

### **Format Validation**

Certain information must follow a required format.

Examples:

* Email addresses must use a valid email format.  
* Contact numbers must follow the accepted format.

### **Uniqueness Validation**

Duplicate records should be prevented when necessary.

Examples:

* Username must be unique.  
* Email address must be unique.  
* Duplicate adoption applications for the same user and pet should not be allowed.

### **Status Validation**

Actions should only be allowed when a record has the correct status.

Examples:

* Only Available pets can receive adoption applications.  
* Only Pending applications can be edited.  
* Only Pending applications can be approved or rejected.  
* Adopted pets cannot receive new applications.

### **Confirmation Validation**

Important actions should require confirmation.

Examples:

* Delete pet  
* Delete user  
* Cancel application  
* Delete adoption record

---

# **13\. Suggested Status Flow**

## **Pet Status Flow**

**Available → Pending Adoption → Adopted**

A pet normally begins as Available.

When an application is approved, the pet becomes Pending Adoption.

Once the adoption process is completed, the pet becomes Adopted.

---

## **Adoption Application Status Flow**

**Pending → Approved**

or

**Pending → Rejected**

or

**Pending → Cancelled**

Applications begin with a Pending status.

Only Admins can approve or reject an application.

Users may cancel their own application while it is still pending.

---

# **14\. Role-Based Access Summary**

| Feature | Regular User | Admin |
| ----- | ----- | ----- |
| Register Account | ✓ | Optional |
| Login / Logout | ✓ | ✓ |
| Manage Own Profile | ✓ | ✓ |
| Browse Pets | ✓ | ✓ |
| View Pet Details | ✓ | ✓ |
| Search Pets | ✓ | ✓ |
| Submit Adoption Application | ✓ | ✗ |
| Update Own Pending Application | ✓ | ✗ |
| Cancel Own Application | ✓ | ✗ |
| View Own Adoption History | ✓ | ✓ |
| Manage Pets | ✗ | ✓ |
| Manage Users | ✗ | ✓ |
| View All Applications | ✗ | ✓ |
| Approve Applications | ✗ | ✓ |
| Reject Applications | ✗ | ✓ |
| Manage Adoption Records | ✗ | ✓ |
| View Complete Adoption History | ✗ | ✓ |
| Search and Filter System Records | Limited | ✓ |

---

# **15\. Overall System Purpose**

The Pet Adoption System is designed to make the adoption process more organized and easier to manage.

Regular Users are given a simple process for finding pets, submitting adoption applications, and checking the progress of their requests.

Admins are provided with tools for managing pets, users, applications, and completed adoptions.

By separating Regular User and Admin permissions, the system provides better security and ensures that important system records can only be changed by authorized users.

