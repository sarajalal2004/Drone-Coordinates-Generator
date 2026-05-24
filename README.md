# A Drone Coordinate Generator API

## 📌 Description
This API system provides comprehensive drone management and image-processing functionality, allowing users to manage drones, process uploaded images for automated coordinate generation, and maintain historical tracking records. 
The platform supports secure CRUD operations, drone rental workflows, and intelligent image-based drone positioning while preserving operational history for future reference and analysis.

---

## 🛠️ Tools & Technologies

- Programming Language: Java  
- Framework: Spring Boot  
- Database: MySQL  
- API Testing: Postman  
- Version Control: Git & GitHub
- Docker

---

## 🧠 Approach

The project was designed as a RESTful API using Spring Boot, focusing on clean architecture and scalability. The application follows a layered structure, separating concerns into controllers, services, and repositories. This made the code easier to maintain and extend.

To handle prioritization, a custom sorting logic was implemented with help of third party Image Processing Libraries. 
These attributes are evaluated and combined into a priority score, which determines the order in which cases are handled.

---

## 🚧 Challenges & Unsolved Problems

One of the main challenges to get the best points for an image and utilize all the drones specified quantity.

---

## 👥 User Stories

  

---

## 🗺️ ERD Diagram

The system includes entities such as FireCase, Location, and ResponseUnit, with relationships defining how incidents are tracked and managed.

[ERD diagram](https://lucid.app/lucidspark/9f044298-82db-44f9-9108-2f0855eeb82b/view)

---

## 📅 Planning & Development Process

The project was broken down into phases:
1. Requirement analysis and user story definition  
2. Database design and ERD creation  
3. API development (CRUD + prioritization logic)  
4. Testing and debugging  
5. Documentation
6. Dockerize 

[GitHub projects dashboard](https://github.com/users/sarajalal2004/projects/4/views/1)

---

## ⚙️ Installation Instructions

Follow these steps to run the project locally:

```bash
# Clone the repository
https://github.com/sarajalal2004/Drone-Coordinates-Generator

# Configure database in application.properties
Fill with your passwords and keys
```
---

## 🌐 API Endpoints

<table>
  <tr>
    <th>HTTP METHOD</th>
    <th>Endpoint</th>
    <th>Access</th>
    <th>Role</th>
  </tr>

  <!-- ================= USER ================= -->
  <tr><td colspan="4" align="center"><strong>👤 User & Profile Endpoints</strong></td></tr>
  <tr><td>POST</td><td>/auth/users/register</td><td>Public</td><td>-</td></tr>
  <tr><td>GET</td><td>/auth/users/verify?token={token}</td><td>Public</td><td>-</td></tr>
  <tr><td>POST</td><td>/auth/users/login</td><td>Public</td><td>-</td></tr>
  <tr><td>POST</td><td>/auth/users/change-password</td><td>Private</td><td>ALL</td></tr>
  <tr><td>POST</td><td>/auth/users/forget-password</td><td>Public</td><td>-</td></tr>
  <tr><td>POST</td><td>/auth/users/reset-password?token={token}&newPassword={newPassword}</td><td>Public</td><td>-</td></tr>
  <tr><td>POST</td><td>/auth/users/inactivate</td><td>Private</td><td>ADMIN</td></tr>
  <tr><td>POST</td><td>/auth/users/reactivate</td><td>Private</td><td>ADMIN</td></tr>
  <tr><td>POST</td><td>/auth/users/update-role</td><td>Private</td><td>ADMIN</td></tr>
  <tr><td>PUT</td><td>/auth/users/update-profile?email={email}</td><td>Private</td><td>ALL</td></tr>
  <tr><td>POST</td><td>/auth/users/update-profile-picture</td><td>Private</td><td>ALL</td></tr>

 <!-- ================= DRONE ================= -->
<tr><td colspan="4" align="center"><strong>🚁 Drone Endpoints</strong></td></tr>
<tr><td>GET</td><td>/api/drones</td><td>Private</td><td>ALL</td></tr>
<tr><td>GET</td><td>/api/drones/{droneId}</td><td>Private</td><td>ALL</td></tr>
<tr><td>POST</td><td>/api/drones</td><td>Private</td><td>MANAGER / ADMIN</td></tr>
<tr><td>PUT</td><td>/api/drones/{droneId}</td><td>Private</td><td>MANAGER / ADMIN</td></tr>
<tr><td>DELETE</td><td>/api/drones/{droneId}</td><td>Private</td><td>ADMIN</td></tr>
<tr><td>PUT</td><td>/api/drones/rent</td><td>Private</td><td>ALL</td></tr>

<!-- ================= DRONE IMAGE PROCESSOR ================= -->
<tr><td colspan="4" align="center"><strong>🖼️ Drone Image Processor Endpoints</strong></td></tr>
<tr><td>POST</td><td>/api/process</td><td>Private</td><td>ALL</td></tr>

<!-- ================= HISTORY ================= -->
<tr><td colspan="4" align="center"><strong>📜 History Endpoints</strong></td></tr>
<tr><td>GET</td><td>/api/histories</td><td>Private</td><td>ALL</td></tr>
<tr><td>GET</td><td>/api/histories/{historyId}</td><td>Private</td><td>ALL</td></tr>
<tr><td>DELETE</td><td>/api/histories/{historyId}</td><td>Private</td><td>ADMIN</td></tr>

</table>



