# User Registration and Login Activity Diagram

```mermaid
flowchart TD
    Start([Start]) --> OpenApp[User opens application]
    OpenApp --> HasAccount{Has account?}
    
    %% REGISTRATION FLOW
    HasAccount -->|No| ClickRegister[Click Register button]
    ClickRegister --> EnterRegData[Enter username, password, confirm password]
    EnterRegData --> ValidateRegData[Validate input data]
    ValidateRegData --> RegDataValid{All valid?}
    
    RegDataValid -->|No - Empty fields| ErrorEmpty[Error: Fill all fields]
    ErrorEmpty --> EndError1([End])
    
    RegDataValid -->|No - Password short| ErrorShort[Error: Password too short]
    ErrorShort --> EndError2([End])
    
    RegDataValid -->|No - Passwords mismatch| ErrorMatch[Error: Passwords don't match]
    ErrorMatch --> EndError3([End])
    
    RegDataValid -->|Yes| CheckUsername[UserDAO.findByUsername username]
    CheckUsername --> UsernameExists{User != null?}
    
    UsernameExists -->|Yes| ErrorUsername[Error: Username taken]
    ErrorUsername --> EndError4([End])
    
    UsernameExists -->|No| CreateUser[User.hash password <br/> new Member username, hashedPassword]
    CreateUser --> InitMemberData[Member: myLibrary = new ArrayList<br/>myCollections = new ArrayList]
    InitMemberData --> SaveUser[UserDAO.addUser user]
    SaveUser --> ShowRegSuccess[Display: Registration successful]
    ShowRegSuccess --> NavToLogin[Navigate to Login screen]
    
    %% LOGIN FLOW
    HasAccount -->|Yes| LoginScreen[Login screen]
    NavToLogin --> LoginScreen
    
    LoginScreen --> EnterLoginData[Enter username and password]
    EnterLoginData --> ValidateLoginData[Validate fields not empty]
    ValidateLoginData --> LoginValid{Valid?}
    
    LoginValid -->|No| ErrorLoginEmpty[Error: Enter credentials]
    ErrorLoginEmpty --> EndError5([End])
    
    LoginValid -->|Yes| FindUser[UserDAO.findByUsername username]
    FindUser --> UserExists{User != null?}
    
    UserExists -->|No| ErrorNotFound[Error: User not found]
    ErrorNotFound --> EndError6([End])
    
    UserExists -->|Yes| VerifyPassword[User.verifyPassword inputPassword]
    VerifyPassword --> PasswordCorrect{returns true?}
    
    PasswordCorrect -->|No| ErrorCreds[Error: Invalid credentials]
    ErrorCreds --> EndError7([End])
    
    PasswordCorrect -->|Yes| CreateToken[Create authentication token]
    CreateToken --> CheckRole[User.getRole]
    CheckRole --> IsAdmin{role == ADMIN?}
    
    IsAdmin -->|Yes| LoadAdminData[Load admin dashboard]
    LoadAdminData --> NavAdmin[Navigate to Admin Dashboard]
    
    IsAdmin -->|No| LoadMemberData[Member: Load myLibrary, myCollections]
    LoadMemberData --> NavMember[Navigate to Member Library]
    
    NavAdmin --> LoggedIn[User logged in successfully]
    NavMember --> LoggedIn
    LoggedIn --> End([End])
```

## Required Methods

### User (Abstract Class)
- `hash(String password): String` - Hash password using BCrypt
- `verifyPassword(String inputPassword): boolean` - Verify password against stored hash
- `getRole(): String` - Return user role (ADMIN or MEMBER)
- `getUsername(): String` - Get username
- `getCreatedAt(): Date` - Get account creation date

### Member (extends User)
- `Member()` - Constructor
- Initialize `myLibrary: List<BookProgress>`
- Initialize `myCollections: List<Collection>`
- `getRole(): String` - Override to return "MEMBER"

### Admin (extends User)
- `Admin()` - Constructor
- `getRole(): String` - Override to return "ADMIN"

### UserDAO
- `findByUsername(String username): User` - Find user by username
- `addUser(User user): void` - Insert new user into database
- `checkUsernameExists(String username): boolean` - Check if username is taken
