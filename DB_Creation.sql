-- -- -- First create the database
-- -- CREATE DATABASE etextbook;
-- -- USE etextbook;

-- -- -- 1. User table (primary table with no dependencies)
-- -- CREATE TABLE User (
-- --     UserID VARCHAR(10) PRIMARY KEY,
-- --     FirstName VARCHAR(50) NOT NULL,
-- --     LastName VARCHAR(50) NOT NULL,
-- --     Email VARCHAR(100) UNIQUE NOT NULL,
-- --     Password VARCHAR(50) NOT NULL,
-- --     Role VARCHAR(10) NOT NULL CHECK (Role IN ('Admin', 'Faculty', 'Student', 'TA')),
-- --     AccountCreationDate DATE NOT NULL
-- -- );

-- -- -- 2. ETextbook table (no foreign key dependencies)
-- -- CREATE TABLE ETextbook (
-- --     TextbookID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     Title VARCHAR(255) NOT NULL,
-- --     TextContent TEXT,
-- --     ImageURL VARCHAR(255),
-- --     CONSTRAINT chk_Content CHECK (TextContent IS NOT NULL OR ImageURL IS NOT NULL)
-- -- );

-- -- -- 3. Chapter table (depends on ETextbook)
-- -- CREATE TABLE Chapter (
-- --     ChapterID VARCHAR(20) PRIMARY KEY,
-- --     ChapterNumber VARCHAR(6) CHECK (ChapterNumber LIKE 'chap_%'),
-- --     Title VARCHAR(255) NOT NULL,
-- --     TextbookID INTEGER NOT NULL,
-- --     FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID) ON DELETE CASCADE,
-- --     UNIQUE (ChapterNumber, TextbookID)
-- -- );

-- -- -- 4. Section table (depends on Chapter)
-- -- CREATE TABLE Section (
-- --     SectionID VARCHAR(10),                                   -- Unique identifier for the section
-- --     TextbookID INTEGER NOT NULL,                             -- Foreign key referring to ETextbook table
-- --     ChapterID VARCHAR(20) NOT NULL,                          -- Allows flexibility in chapter ID format
-- --     SectionNumber VARCHAR(10) NOT NULL,                      -- Section number identifier
-- --     Title VARCHAR(255) NOT NULL,                             -- Title of the section

-- --     -- Composite primary key for SectionNumber, ChapterID, and TextbookID
-- --     PRIMARY KEY (SectionNumber, ChapterID, TextbookID),
    
-- --     -- Foreign key constraints with cascading delete
-- --     FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID) ON DELETE CASCADE,
-- --     FOREIGN KEY (ChapterID) REFERENCES Chapter(ChapterID) ON DELETE CASCADE,
    
-- --     -- Unique constraint on Title per Chapter and Textbook
-- --     UNIQUE (Title, ChapterID, TextbookID)
-- -- );
-- -- -- 5. ContentBlock table (depends on Section)
-- -- CREATE TABLE ContentBlock (
-- -- TextbookID INTEGER NOT NULL, -- Represents the textbook ID
-- -- ChapterID VARCHAR(20) NOT NULL, -- Represents the chapter ID
-- -- SectionNumber VARCHAR(10) NOT NULL, -- Represents the section number
-- -- ContentBlockID VARCHAR(255) NOT NULL, -- String type for block ID
-- -- ContentType VARCHAR(10) NOT NULL CHECK (ContentType IN ('Text', 'Activity', 'Picture')), -- Content types
-- -- Content TEXT, -- Text field for storing content
-- -- PRIMARY KEY (TextbookID, ChapterID, SectionNumber, ContentBlockID), -- Composite primary key
-- -- -- Composite foreign key referencing Section (TextbookID, ChapterID, SectionNumber)
-- -- FOREIGN KEY (SectionNumber, ChapterID, TextbookID) 
-- -- REFERENCES Section(SectionNumber, ChapterID, TextbookID) ON DELETE CASCADE,
-- -- CHECK (
-- -- (ContentType = 'Text' AND Content IS NOT NULL) OR 
-- -- (ContentType = 'Activity' AND Content IS NOT NULL) OR 
-- -- (ContentType = 'Picture' AND Content IS NOT NULL) -- Ensure non-null content for each type
-- -- )
-- -- );
-- -- -- 6. Activity table (depends on ContentBlock)
-- -- CREATE TABLE Activity (
-- --     ActivityID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     Question TEXT NOT NULL,
-- --     CorrectAnswer VARCHAR(255) NOT NULL,
-- --     IncorrectAnswer1 VARCHAR(255) NOT NULL,
-- --     IncorrectAnswer2 VARCHAR(255) NOT NULL,
-- --     IncorrectAnswer3 VARCHAR(255) NOT NULL,
-- --     ExplanationCorrect TEXT,
-- --     ExplanationIncorrect1 TEXT,
-- --     ExplanationIncorrect2 TEXT,
-- --     ExplanationIncorrect3 TEXT,
-- --     ContentBlockID VARCHAR(255) NOT NULL,
-- --     FOREIGN KEY (ContentBlockID) REFERENCES ContentBlock(ContentBlockID) ON DELETE CASCADE,
-- --     UNIQUE (ActivityID, ContentBlockID)
-- -- );

-- -- -- 7. Course table (depends on User and ETextbook)
-- -- CREATE TABLE Course (
-- --     CourseID VARCHAR(20) PRIMARY KEY,
-- --     Title VARCHAR(255) NOT NULL,
-- --     CourseType VARCHAR(10) NOT NULL CHECK (CourseType IN ('Active', 'Evaluation')),
-- --     Token VARCHAR(7),
-- --     Capacity INTEGER,
-- --     StartDate DATE NOT NULL,
-- --     EndDate DATE NOT NULL,
-- --     FacultyID VARCHAR(10) NOT NULL,
-- --     TextbookID INTEGER,
-- --     FOREIGN KEY (FacultyID) REFERENCES User(UserID) ON DELETE CASCADE,
-- --     FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID) ON DELETE CASCADE,
-- --     CHECK ((CourseType = 'Active' AND Token IS NOT NULL AND Capacity IS NOT NULL) OR
-- --           (CourseType = 'Evaluation' AND Token IS NULL AND Capacity IS NULL))
-- -- );

-- -- -- 8. Enrollment table (depends on User and Course)
-- -- CREATE TABLE Enrollment (
-- --     EnrollmentID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     StudentID VARCHAR(10) NOT NULL,
-- --     CourseID VARCHAR(20) NOT NULL,
-- --     Status VARCHAR(10) NOT NULL CHECK (Status IN ('Pending', 'Approved')),
-- --     RequestDate DATE NOT NULL,
-- --     ApprovalDate DATE,
-- --     FOREIGN KEY (StudentID) REFERENCES User(UserID) ON DELETE CASCADE,
-- --     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
-- -- );

-- -- -- 9. Notification table (depends on User)
-- -- CREATE TABLE Notification (
-- --     NotificationID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     UserID VARCHAR(10) NOT NULL,
-- --     Message TEXT NOT NULL,
-- --     NotificationDate DATETIME DEFAULT CURRENT_TIMESTAMP,
-- --     IsRead BOOLEAN DEFAULT FALSE,
-- --     FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
-- -- );

-- -- -- 10. CourseTA table (depends on Course and User)
-- -- CREATE TABLE CourseTA (
-- --     CourseTAID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     CourseID VARCHAR(20) NOT NULL,
-- --     TAID VARCHAR(10) NOT NULL,
-- --     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE,
-- --     FOREIGN KEY (TAID) REFERENCES User(UserID) ON DELETE CASCADE
-- -- );

-- -- -- 11. CourseCustomization table (depends on Course, ContentBlock, Activity, and User)
-- -- CREATE TABLE CourseCustomization (
-- --     CustomizationID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     CourseID VARCHAR(20) NOT NULL,
-- --     ContentBlockID VARCHAR(255),
-- --     ActivityID INTEGER,
-- --     IsHidden BOOLEAN DEFAULT FALSE,
-- --     AddedByRole VARCHAR(10) NOT NULL CHECK (AddedByRole IN ('Faculty', 'TA')),
-- --     IsOriginalContent BOOLEAN DEFAULT TRUE,
-- --     DisplayOrder INTEGER NOT NULL,
-- --     CreatedByUserID VARCHAR(10) NOT NULL,
-- --     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE,
-- --     FOREIGN KEY (ContentBlockID) REFERENCES ContentBlock(ContentBlockID) ON DELETE CASCADE,
-- --     FOREIGN KEY (ActivityID) REFERENCES Activity(ActivityID) ON DELETE CASCADE,
-- --     FOREIGN KEY (CreatedByUserID) REFERENCES User(UserID) ON DELETE CASCADE
-- -- );

-- -- -- 12. StudentActivity table (depends on User, Activity, and Course)
-- -- CREATE TABLE StudentActivity (
-- --     StudentActivityID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     StudentID VARCHAR(10) NOT NULL,
-- --     ActivityID INTEGER NOT NULL,
-- --     CourseID VARCHAR(20) NOT NULL,
-- --     AttemptDate DATETIME DEFAULT CURRENT_TIMESTAMP,
-- --     Score INTEGER CHECK (Score BETWEEN 0 AND 3),
-- --     FOREIGN KEY (StudentID) REFERENCES User(UserID) ON DELETE CASCADE,
-- --     FOREIGN KEY (ActivityID) REFERENCES Activity(ActivityID) ON DELETE CASCADE,
-- --     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
-- -- );

-- -- -- 13. ParticipationPoints table (depends on User and Course)
-- -- CREATE TABLE ParticipationPoints (
-- --     ParticipationID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     StudentID VARCHAR(10) NOT NULL,
-- --     CourseID VARCHAR(20) NOT NULL,
-- --     TotalPoints INTEGER DEFAULT 0,
-- --     MaxPoints INTEGER DEFAULT 0,
-- --     FOREIGN KEY (StudentID) REFERENCES User(UserID) ON DELETE CASCADE,
-- --     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
-- -- );

-- -- -- 14. CourseContentVersioning table (depends on Course, Chapter, and Section)
-- -- CREATE TABLE CourseContentVersioning (
-- --     VersionID INTEGER PRIMARY KEY AUTO_INCREMENT,
-- --     CourseID VARCHAR(20) NOT NULL,
-- --     ChapterID INTEGER NOT NULL,
-- --     SectionID INTEGER,
-- --     DisplayOrder INTEGER NOT NULL,
-- --     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE,
-- --     FOREIGN KEY (ChapterID) REFERENCES Chapter(ChapterID) ON DELETE CASCADE,
-- --     FOREIGN KEY (SectionID) REFERENCES Section(SectionID) ON DELETE CASCADE
-- -- );

-- -- First create the database
-- CREATE DATABASE etextbook;
-- USE etextbook;

-- -- 1. User table (no changes needed)
-- CREATE TABLE User (
--     UserID VARCHAR(10) PRIMARY KEY,
--     FirstName VARCHAR(50) NOT NULL,
--     LastName VARCHAR(50) NOT NULL,
--     Email VARCHAR(100) UNIQUE NOT NULL,
--     Password VARCHAR(50) NOT NULL,
--     Role VARCHAR(10) NOT NULL CHECK (Role IN ('Admin', 'Faculty', 'Student', 'TA')),
--     AccountCreationDate DATE NOT NULL
-- );

-- -- 2. ETextbook table (no changes needed)
-- CREATE TABLE ETextbook (
--     TextbookID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     Title VARCHAR(255) NOT NULL,
--     TextContent TEXT,
--     ImageURL VARCHAR(255),
--     CONSTRAINT chk_Content CHECK (TextContent IS NOT NULL OR ImageURL IS NOT NULL)
-- );

-- -- 3. Chapter table (fixed AUTO_INCREMENT data type)
-- CREATE TABLE Chapter (
--     ChapterID VARCHAR(6) NOT NULL,  -- Changed from INTEGER to VARCHAR(6) to store 'chap01' format
--     TextbookID INTEGER NOT NULL,
--     Title VARCHAR(255) NOT NULL,
--     Hidden BOOLEAN DEFAULT FALSE,
--     PRIMARY KEY (TextbookID, ChapterID),  -- Changed to composite key
--     FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID) ON DELETE CASCADE
-- );

-- -- 4. Section table (fixed composite key structure)
-- CREATE TABLE Section (
--     TextbookID INTEGER NOT NULL,
--     ChapterID VARCHAR(6) NOT NULL,    -- Changed to match Chapter table
--     SectionID VARCHAR(10) NOT NULL,   -- Changed to store 'Sec01' format
--     Title VARCHAR(255) NOT NULL,
--     Hidden BOOLEAN DEFAULT FALSE,
--     PRIMARY KEY (TextbookID, ChapterID, SectionID),
--     FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID) ON DELETE CASCADE,
--     FOREIGN KEY (TextbookID, ChapterID) REFERENCES Chapter(TextbookID, ChapterID) ON DELETE CASCADE
-- );

-- -- 5. ContentBlock table (fixed composite key references)
-- CREATE TABLE ContentBlock (
--     TextbookID INTEGER NOT NULL,
--     ChapterID VARCHAR(6) NOT NULL,     -- Changed to match Chapter table
--     SectionNumber VARCHAR(10) NOT NULL, -- Changed to store 'Sec01' format
--     BlockID VARCHAR(20) NOT NULL,      -- Changed to store 'Block01' format
--     ContentType VARCHAR(10) NOT NULL CHECK (ContentType IN ('text', 'activity', 'picture')),
--     Content TEXT,
--     Hidden BOOLEAN DEFAULT FALSE,
--     PRIMARY KEY (TextbookID, ChapterID, SectionNumber, BlockID),
--     FOREIGN KEY (TextbookID, ChapterID, SectionNumber)
--         REFERENCES Section(TextbookID, ChapterID, SectionID) ON DELETE CASCADE
-- );

-- -- 6. Activity table (fixed foreign key reference)
-- CREATE TABLE Activity (
--     ActivityID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     Question TEXT NOT NULL,
--     CorrectAnswer VARCHAR(255) NOT NULL,
--     IncorrectAnswer1 VARCHAR(255) NOT NULL,
--     IncorrectAnswer2 VARCHAR(255) NOT NULL,
--     IncorrectAnswer3 VARCHAR(255) NOT NULL,
--     ExplanationCorrect TEXT,
--     ExplanationIncorrect1 TEXT,
--     ExplanationIncorrect2 TEXT,
--     ExplanationIncorrect3 TEXT,
--     ContentBlockID INTEGER NOT NULL,
--     FOREIGN KEY (ContentBlockID) REFERENCES ContentBlock(ContentBlockID) ON DELETE CASCADE
-- );

-- -- 7. Course table (no changes needed)
-- CREATE TABLE Course (
--     CourseID VARCHAR(20) PRIMARY KEY,
--     Title VARCHAR(255) NOT NULL,
--     CourseType VARCHAR(10) NOT NULL CHECK (CourseType IN ('Active', 'Evaluation')),
--     Token VARCHAR(7),
--     Capacity INTEGER,
--     StartDate DATE NOT NULL,
--     EndDate DATE NOT NULL,
--     FacultyID VARCHAR(10) NOT NULL,
--     TextbookID INTEGER,
--     FOREIGN KEY (FacultyID) REFERENCES User(UserID) ON DELETE CASCADE,
--     FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID) ON DELETE CASCADE,
--     CHECK ((CourseType = 'Active' AND Token IS NOT NULL AND Capacity IS NOT NULL) OR
--         (CourseType = 'Evaluation' AND Token IS NULL AND Capacity IS NULL))
-- );

-- -- 8. Enrollment table (no changes needed)
-- CREATE TABLE Enrollment (
--     EnrollmentID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     StudentID VARCHAR(10) NOT NULL,
--     CourseID VARCHAR(20) NOT NULL,
--     Status VARCHAR(10) NOT NULL CHECK (Status IN ('Pending', 'Approved')),
--     RequestDate DATE NOT NULL,
--     ApprovalDate DATE,
--     FOREIGN KEY (StudentID) REFERENCES User(UserID) ON DELETE CASCADE,
--     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
-- );

-- -- 9. Notification table (no changes needed)
-- CREATE TABLE Notification (
--     NotificationID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     UserID VARCHAR(10) NOT NULL,
--     Message TEXT NOT NULL,
--     NotificationDate DATETIME DEFAULT CURRENT_TIMESTAMP,
--     IsRead BOOLEAN DEFAULT FALSE,
--     FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
-- );

-- -- 10. CourseTA table (no changes needed)
-- CREATE TABLE CourseTA (
--     CourseTAID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     CourseID VARCHAR(20) NOT NULL,
--     TAID VARCHAR(10) NOT NULL,
--     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE,
--     FOREIGN KEY (TAID) REFERENCES User(UserID) ON DELETE CASCADE
-- );

-- -- 11. CourseCustomization table (fixed foreign key references)
-- CREATE TABLE CourseCustomization (
--     CustomizationID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     CourseID VARCHAR(20) NOT NULL,
--     ContentBlockID INTEGER,
--     ActivityID INTEGER,
--     IsHidden BOOLEAN DEFAULT FALSE,
--     AddedByRole VARCHAR(10) NOT NULL CHECK (AddedByRole IN ('Faculty', 'TA')),
--     IsOriginalContent BOOLEAN DEFAULT TRUE,
--     DisplayOrder INTEGER NOT NULL,
--     CreatedByUserID VARCHAR(10) NOT NULL,
--     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE,
--     FOREIGN KEY (ContentBlockID) REFERENCES ContentBlock(ContentBlockID) ON DELETE CASCADE,
--     FOREIGN KEY (ActivityID) REFERENCES Activity(ActivityID) ON DELETE CASCADE,
--     FOREIGN KEY (CreatedByUserID) REFERENCES User(UserID) ON DELETE CASCADE
-- );

-- -- 12. StudentActivity table (no changes needed)
-- CREATE TABLE StudentActivity (
--     StudentActivityID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     StudentID VARCHAR(10) NOT NULL,
--     ActivityID INTEGER NOT NULL,
--     CourseID VARCHAR(20) NOT NULL,
--     AttemptDate DATETIME DEFAULT CURRENT_TIMESTAMP,
--     Score INTEGER CHECK (Score BETWEEN 0 AND 3),
--     FOREIGN KEY (StudentID) REFERENCES User(UserID) ON DELETE CASCADE,
--     FOREIGN KEY (ActivityID) REFERENCES Activity(ActivityID) ON DELETE CASCADE,
--     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
-- );

-- -- 13. ParticipationPoints table (no changes needed)
-- CREATE TABLE ParticipationPoints (
--     ParticipationID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     StudentID VARCHAR(10) NOT NULL,
--     CourseID VARCHAR(20) NOT NULL,
--     TotalPoints INTEGER DEFAULT 0,
--     MaxPoints INTEGER DEFAULT 0,
--     FOREIGN KEY (StudentID) REFERENCES User(UserID) ON DELETE CASCADE,
--     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
-- );

-- -- 14. CourseContentVersioning table (fixed data types)
-- CREATE TABLE CourseContentVersioning (
--     VersionID INTEGER PRIMARY KEY AUTO_INCREMENT,
--     CourseID VARCHAR(20) NOT NULL,
--     ChapterID INTEGER NOT NULL,
--     SectionID INTEGER,
--     DisplayOrder INTEGER NOT NULL,
--     FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE,
--     FOREIGN KEY (ChapterID) REFERENCES Chapter(ChapterID) ON DELETE CASCADE,
--     FOREIGN KEY (SectionID) REFERENCES Section(SectionID) ON DELETE CASCADE
-- );



-- Create database
CREATE DATABASE etextbook;
USE etextbook;

-- 1. User table (base table for all users)
CREATE TABLE User (
    UserID VARCHAR(10) PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(50) NOT NULL,
    Role VARCHAR(10) NOT NULL CHECK (Role IN ('Admin', 'Faculty', 'Student', 'TA')),
    AccountCreationDate DATE NOT NULL
);

-- 2. Faculty table (extends User)
CREATE TABLE Faculty (
    FacultyID VARCHAR(10) PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(50) NOT NULL,
    FOREIGN KEY (FacultyID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- 3. TA table (extends User)
CREATE TABLE TA (
    TAID VARCHAR(10) PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(50) NOT NULL,
    CourseID VARCHAR(20),
    FacultyID VARCHAR(10),
    FOREIGN KEY (TAID) REFERENCES User(UserID) ON DELETE CASCADE,
    FOREIGN KEY (FacultyID) REFERENCES Faculty(FacultyID) ON DELETE SET NULL
);

-- 4. Student table (extends User)
CREATE TABLE Student (
    StudentID VARCHAR(10) PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(50) NOT NULL,
    FOREIGN KEY (StudentID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- 5. ETextbook table
CREATE TABLE ETextbook (
    TextbookID INTEGER PRIMARY KEY,
    Title VARCHAR(255) NOT NULL
);

-- 6. Chapter table
CREATE TABLE Chapter (
    TextbookID INTEGER NOT NULL,
    ChapterID VARCHAR(6) NOT NULL CHECK (ChapterID LIKE 'chap_%'),
    Title VARCHAR(255) NOT NULL,
    Hidden BOOLEAN DEFAULT FALSE,
    DisplayOrder INTEGER NOT NULL,
    PRIMARY KEY (TextbookID, ChapterID),
    FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID) ON DELETE CASCADE,
    UNIQUE (TextbookID, DisplayOrder)
);

-- 7. Section table
CREATE TABLE Section (
    TextbookID INTEGER NOT NULL,
    ChapterID VARCHAR(6) NOT NULL,
    SectionID VARCHAR(10) NOT NULL CHECK (SectionID LIKE 'Sec%'),
    Title VARCHAR(255) NOT NULL,
    Hidden BOOLEAN DEFAULT FALSE,
    DisplayOrder INTEGER NOT NULL,
    PRIMARY KEY (TextbookID, ChapterID, SectionID),
    FOREIGN KEY (TextbookID, ChapterID) REFERENCES Chapter(TextbookID, ChapterID) ON DELETE CASCADE,
    UNIQUE (TextbookID, ChapterID, DisplayOrder)
);

-- 8. ContentBlock table
CREATE TABLE ContentBlock (
    TextbookID INTEGER NOT NULL,
    ChapterID VARCHAR(6) NOT NULL,
    SectionID VARCHAR(10) NOT NULL,
    BlockID VARCHAR(10) NOT NULL CHECK (BlockID LIKE 'Block%'),
    ContentType VARCHAR(10) NOT NULL CHECK (ContentType IN ('text', 'activity', 'picture')),
    Content TEXT NOT NULL,
    Hidden BOOLEAN DEFAULT FALSE,
    DisplayOrder INTEGER NOT NULL,
    CreatedByUserID VARCHAR(10),
    PRIMARY KEY (TextbookID, ChapterID, SectionID, BlockID),
    FOREIGN KEY (TextbookID, ChapterID, SectionID) 
        REFERENCES Section(TextbookID, ChapterID, SectionID) ON DELETE CASCADE,
    FOREIGN KEY (CreatedByUserID) REFERENCES User(UserID) ON DELETE SET NULL,
    UNIQUE (TextbookID, ChapterID, SectionID, DisplayOrder)
);

-- 9. Activity table
CREATE TABLE Activity (
    TextbookID INTEGER NOT NULL,
    ChapterID VARCHAR(6) NOT NULL,
    SectionID VARCHAR(10) NOT NULL,
    BlockID VARCHAR(10) NOT NULL,
    UniqueActivityID VARCHAR(4) NOT NULL CHECK (UniqueActivityID LIKE 'ACT%'),
    Hidden BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (TextbookID, ChapterID, SectionID, BlockID, UniqueActivityID),
    FOREIGN KEY (TextbookID, ChapterID, SectionID, BlockID) 
        REFERENCES ContentBlock(TextbookID, ChapterID, SectionID, BlockID) ON DELETE CASCADE
);

-- 10. Question table
CREATE TABLE Question (
    TextbookID INTEGER NOT NULL,
    ChapterID VARCHAR(6) NOT NULL,
    SectionID VARCHAR(10) NOT NULL,
    BlockID VARCHAR(10) NOT NULL,
    UniqueActivityID VARCHAR(4) NOT NULL,
    QuestionID VARCHAR(2) NOT NULL CHECK (QuestionID LIKE 'Q%'),
    QuestionText TEXT NOT NULL,
    Option1 TEXT NOT NULL,
    Option1Explanation TEXT NOT NULL,
    Option2 TEXT NOT NULL,
    Option2Explanation TEXT NOT NULL,
    Option3 TEXT NOT NULL,
    Option3Explanation TEXT NOT NULL,
    Option4 TEXT NOT NULL,
    Option4Explanation TEXT NOT NULL,
    Answer INTEGER NOT NULL CHECK (Answer BETWEEN 1 AND 4),
    PRIMARY KEY (TextbookID, ChapterID, SectionID, BlockID, UniqueActivityID, QuestionID),
    FOREIGN KEY (TextbookID, ChapterID, SectionID, BlockID, UniqueActivityID) 
        REFERENCES Activity(TextbookID, ChapterID, SectionID, BlockID, UniqueActivityID) ON DELETE CASCADE
);

-- 11. Course table
CREATE TABLE Course (
    CourseID VARCHAR(20) PRIMARY KEY,
    Title VARCHAR(255) NOT NULL,
    TextbookID INTEGER NOT NULL,
    CourseType VARCHAR(10) NOT NULL CHECK (CourseType IN ('Active', 'Evaluation')),
    FacultyID VARCHAR(10) NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Token VARCHAR(7),
    Capacity INTEGER,
    FOREIGN KEY (TextbookID) REFERENCES ETextbook(TextbookID),
    FOREIGN KEY (FacultyID) REFERENCES Faculty(FacultyID),
    CHECK ((CourseType = 'Active' AND Token IS NOT NULL AND Capacity IS NOT NULL) OR
        (CourseType = 'Evaluation' AND Token IS NULL AND Capacity IS NULL))
);

-- 12. CourseTA table
CREATE TABLE CourseTA (
    CourseID VARCHAR(20) NOT NULL,
    TAID VARCHAR(10) NOT NULL,
    PRIMARY KEY (CourseID, TAID),
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE,
    FOREIGN KEY (TAID) REFERENCES TA(TAID) ON DELETE CASCADE
);

-- 13. Enrollment table
CREATE TABLE Enrollment (
    StudentID VARCHAR(10) NOT NULL,
    CourseID VARCHAR(20) NOT NULL,
    Status VARCHAR(10) NOT NULL CHECK (Status IN ('Pending', 'Enrolled')),
    RequestDate DATE NOT NULL,
    ApprovalDate DATE,
    PRIMARY KEY (StudentID, CourseID),
    FOREIGN KEY (StudentID) REFERENCES Student(StudentID) ON DELETE CASCADE,
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID) ON DELETE CASCADE
);

-- 14. StudentActivity table
CREATE TABLE StudentActivity (
    StudentID VARCHAR(10) NOT NULL,
    CourseID VARCHAR(20) NOT NULL,
    TextbookID INTEGER NOT NULL,
    ChapterID VARCHAR(6) NOT NULL,
    SectionID VARCHAR(10) NOT NULL,
    BlockID VARCHAR(10) NOT NULL,
    UniqueActivityID VARCHAR(4) NOT NULL,
    QuestionID VARCHAR(2) NOT NULL,
    Points INTEGER CHECK (Points BETWEEN 0 AND 3),
    AttemptTimestamp DATETIME NOT NULL,
    PRIMARY KEY (StudentID, CourseID, TextbookID, ChapterID, SectionID, BlockID, UniqueActivityID, QuestionID),
    FOREIGN KEY (StudentID, CourseID) REFERENCES Enrollment(StudentID, CourseID),
    FOREIGN KEY (TextbookID, ChapterID, SectionID, BlockID, UniqueActivityID, QuestionID) 
        REFERENCES Question(TextbookID, ChapterID, SectionID, BlockID, UniqueActivityID, QuestionID)
);

-- 15. ParticipationPoints table
CREATE TABLE ParticipationPoints (
    StudentID VARCHAR(10) NOT NULL,
    CourseID VARCHAR(20) NOT NULL,
    TotalPoints INTEGER DEFAULT 0,
    NumFinishedActivities INTEGER DEFAULT 0,
    PRIMARY KEY (StudentID, CourseID),
    FOREIGN KEY (StudentID, CourseID) REFERENCES Enrollment(StudentID, CourseID)
);

-- 16. Notification table
CREATE TABLE Notification (
    NotificationID INTEGER PRIMARY KEY AUTO_INCREMENT,
    UserID VARCHAR(10) NOT NULL,
    Message TEXT NOT NULL,
    NotificationDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    IsRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (UserID) REFERENCES User(UserID) ON DELETE CASCADE
);

-- 17. ContentVersioning table
CREATE TABLE ContentVersioning (
    VersionID INTEGER PRIMARY KEY AUTO_INCREMENT,
    TextbookID INTEGER NOT NULL,
    ChapterID VARCHAR(6) NOT NULL,
    SectionID VARCHAR(10) NOT NULL,
    BlockID VARCHAR(10) NOT NULL,
    CourseID VARCHAR(20) NOT NULL,
    IsHidden BOOLEAN DEFAULT FALSE,
    DisplayOrder INTEGER NOT NULL,
    CreatedByUserID VARCHAR(10) NOT NULL,
    FOREIGN KEY (TextbookID, ChapterID, SectionID, BlockID) 
        REFERENCES ContentBlock(TextbookID, ChapterID, SectionID, BlockID),
    FOREIGN KEY (CourseID) REFERENCES Course(CourseID),
    FOREIGN KEY (CreatedByUserID) REFERENCES User(UserID)
);