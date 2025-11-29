Social Anti-Fake News System Phase II

Group Information

Group Name: child
TianZiming（Rushford） 20232066 leader
JinYitong(S1mple) 20232035 member
YanWeisen(Rance) 20232038 member


Project Description
The Social Anti-Fake News system is a system that requires the wisdom of the cloud to help detect fake news. The users who heard the news can enter the news into the system, so the other users can see all the news. The other users can look at the news and vote whether this news is fake or not, and put their comments on why they think this is fake or not. The news is considered fake or not fake by the number of votes. So on the list of news page, users can filter all news, fake news, or non-fake news.


Features

Home Page & News Management
The system consists of the home page, which will show the list of news and a filter to select what kind of news to see. The user must be able to select the amount of news they want to see on each page.
The details of each news story on this home page must include the news topic, a short detail of the news, the status of the news, whether fake or not, the name of the reporter, and the date and time of the report.

News Details & Interaction
When the user selects the news that they want to see the details, the application should show the new with the full details such as the news topic, a short detail of the news, the status of the news, whether fake or not fake, the name of the reporter, the date of time of the report, and upload the image of the event.
From this page, the user can open the list of comments and the results that other people have voted and commented on. In this part, the pagination must be applied.
The user can click on another page to vote on the news, the user has to vote whether the news is fake or not, and add a comment or an image that you think can convince your choice.

User Roles & Authentication
- Registration: The user can register their information and initially the reader. The register information must include name, surname, email address, password, and profile image.
- Reader: The reader only can see the news but can vote but cannot send the news.
- Member: The administrator can set the selected user to be the member. The members can vote the news and also submit the new news.
- Administrator: The administrator can remove the news or remove some comments which is not proper. When the news is removed, no one except the administrator can see it. If the vote has been removed the score must be recalculated using the score which is still available.

Search
All user can search by all details of the news (topic name, short details of the news, name of the reporter) or search by the status of the news.

Unique Features
- AI Analysis: The system integrates AI to analyze news content, providing an automated credibility assessment to assist users in their judgment.
- Interactive Charts: Real-time Pie Charts visualize the voting distribution (Fake vs. Real), giving users an immediate understanding of community consensus.


Technology Stack

The application is implemented using the following technologies:

- Frontend: Vue.js 3 (Composition API), Tailwind CSS, Pinia, Vue Router, Axios.
- Backend: Java 17, Spring Boot 3.x, Spring Security (JWT), Spring Data JPA.
- Database: MySQL 8.0.
- Storage: Supabase Storage (S3 Protocol) for image uploads.
- DevOps: Docker, GitHub Actions (CI/CD).

Deployment & CI/CD

- Deployment Address: http://119.91.69.247/
- CI/CD Pipeline: The automated CD must be set up. We use GitHub Actions to automatically build and deploy the application to our Tencent Cloud VM whenever changes are pushed to the `main` branch.


Repositories:
- Frontend & Backend: https://github.com/chartchai-class/project-02-anti-fakenews-cn-child.git

Presentation Video
- YouTube video link：https://www.youtube.com/watch?v=dUxJ-FRz5rw


