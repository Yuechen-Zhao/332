-- Yuechen Zhao
-- 1126125
-- CSE 344 Homework2

-- In order to speed up the join in Part C Question 1, 3, and 5.
CREATE UNIQUE INDEX a_id ON Actor(id);

-- In order to speed up the join in C 1, 2, 3, and 5.
CREATE UNIQUE INDEX m_id ON Movie(id);

-- In order to speed up the join in C 2 and 4.
CREATE UNIQUE INDEX d_id ON Directors(id);

-- In order to speed up the join in C 1, 3, and 5.
CREATE INDEX c_pid ON Casts(pid);

-- In order to speed up the join in C 1, 3, and 5.
CREATE INDEX c_mid ON Casts(mid);

-- In order to speed up the join in C 2 and 4.
CREATE INDEX md_did ON Movie_directors(did);

-- In order to speed up the join in C 2.
CREATE INDEX md_mid ON Movie_directors(mid);

-- In order to speed up the join in C 2.
CREATE INDEX g_mid ON Genre(mid);