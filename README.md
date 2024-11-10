## Simple S3 Storage

- Users can log in to upload, download and search their files.
- First create a user and log in with it. Use the Set-Cookie header sent in the login response to set cookie for mainaining session.
- Sending Cookie in requests is mandatory for Storage APIs.

### Steps to run the server
- `mvn clean install`
- Run the jar file in target folder.
- Use the postman collection to navigate, upload and download files.