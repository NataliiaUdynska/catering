Catering

Catering is a Spring Boot–based web application for catering services. 
The project allows users to browse the homepage, about page, and menu, submit requests via a contact form, manage their orders, and receive invoices by email in PDF format.

Additional features
- Contact form with server validation
- Display of messages about successful actions without reloading the page
- Secure authentication and authorisation
- Role differentiation (USER / ADMIN)
- Clean MVC architecture
- Adaptive and neat page design


![img.png](img.png)
![img_1.png](img_1.png)
Home page

![img_2.png](img_2.png)
![img_3.png](img_3.png)
About Us page

![img_4.png](img_4.png)
![img_5.png](img_5.png)
Contacts page

![img_6.png](img_6.png)
![img_8.png](img_8.png)
Menu page

![img_9.png](img_9.png)
Login page

![img_10.png](img_10.png)
Register page

![img_11.png](img_11.png)
Profile page(User)

![img_12.png](img_12.png)
Cart page

![img_13.png](img_13.png)
Order page

![img_15.png](img_15.png)
Profile page(Order History)

![img_16.png](img_16.png)
Profile page(Admin)

![img_17.png](img_17.png)
Admin/orders page

![img_18.png](img_18.png)
Invoice page

![img_19.png](img_19.png)
![img_20.png](img_20.png)
Admin/menu page

![img_21.png](img_21.png)
![img_22.png](img_22.png)
Admin/menu/edit page

![img_23.png](img_23.png)
Error page



Technologies
- Java: 21
- Spring Boot: 3.3.5
- Spring MVC: 6.x 
- Spring Security: 6.x
- Spring Data JPA: 3.x
- Hibernate: 6.x
- Thymeleaf: 3.x
- Thymeleaf Extras (Spring Security): 3.1.x
- Spring Validation: 3.x
- Maven: 3.9+
- HTML5 / CSS3
- JavaScript: Basic
- H2 Database: Runtime (development)
- JavaMailSender: Spring Boot Starter Mail
- PDF Generation: iText 7.2.5 (Invoice generation)


User capabilities:
- View the main page with general information about the service
- Read the ‘About us’ page
- View the catering menu
- Send requests via the contact form
- Register and log in to the system
- Create catering orders
- View their order history
- Receive invoices by email in PDF format

Administrator capabilities:
- Access the admin panel
- View a list of all users
- Manage user orders
- Change order status
- Generate and send invoices in PDF format and view them
- Manage the menu (add, edit, delete items)
- Control user and order data


How to run the project:
1. Clone the repository `git clone https://github.com/NataliiaUdynska/catering.git`
2. Navigate `cd catering`
3. Run `mvn spring-boot:run`
3. Open `http://localhost:8080`