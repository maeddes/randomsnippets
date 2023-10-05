brew install libpq
echo 'set -g fish_user_paths "/usr/local/opt/libpq/bin" $fish_user_paths' >> ~/.config/fish/config.fish

new shell

psql -h localhost -p 5432 -d mydb -U matthias --password

mydb-# \l
                                 List of databases
   Name    |  Owner   | Encoding |  Collate   |   Ctype    |   Access privileges
-----------+----------+----------+------------+------------+-----------------------
 mydb      | matthias | UTF8     | en_US.utf8 | en_US.utf8 |
 postgres  | matthias | UTF8     | en_US.utf8 | en_US.utf8 |
 template0 | matthias | UTF8     | en_US.utf8 | en_US.utf8 | =c/matthias          +
           |          |          |            |            | matthias=CTc/matthias
 template1 | matthias | UTF8     | en_US.utf8 | en_US.utf8 | =c/matthias          +
           |          |          |            |            | matthias=CTc/matthias
(4 rows)

mydb-# \dt
        List of relations
 Schema | Name | Type  |  Owner
--------+------+-------+----------
 public | todo | table | matthias
(1 row)

mydb=# \d
        List of relations
 Schema | Name | Type  |  Owner
--------+------+-------+----------
 public | todo | table | matthias
(1 row)

mydb=# \d todo
                       Table "public.todo"
 Column |          Type          | Collation | Nullable | Default
--------+------------------------+-----------+----------+---------
 todo   | character varying(255) |           | not null |
Indexes:
    "todo_pkey" PRIMARY KEY, btree (todo)

mydb=# SELECT * FROM todo;
   todo
-----------
 NEW_TODO3
(1 row)
