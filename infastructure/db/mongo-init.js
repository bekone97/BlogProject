db.createUser(
    {
        user: "SomeUser",
        pwd: "SomePassword",
        roles: [
            {
                role: "readWrite",
                db: "blog_db"
            }
        ]
    }
);