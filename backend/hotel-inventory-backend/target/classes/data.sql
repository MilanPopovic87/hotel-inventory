-- =========================
-- USERS (excluding admin, created by @PostConstruct)
-- =========================
INSERT INTO users (username, password, role)
VALUES ('demo', '$2a$10$ljwIPHLqr/VDtOQGEsGyueLwBkWg2zDy1kVp3VKJOCJehg3C7HNe6', 'USER'),
       ('alice', '$2a$10$ItpLgDoFfNVdUwnu7ZeosucNkZfzPbM5q0BRjPG5/fmllb0xP47kq', 'USER'),
       ('bob', '$2a$10$7UL8loOO.X8Ikgil7k3yT.b9srRPm2KnnSvEzk1Q.dmyDkn4R6WJO', 'USER'),
       ('charlie', '$2a$10$L3eTar1baACISifoe1IgXek/3LwHGv.YPBdlJhyFCcnd.rAHdm30C', 'USER'),
       ('diana', '$2a$10$xtonflIkHMW7vWKte1XvJ.2LGW.r1fZUeKmSi73iuwMx523d5cf5S', 'USER');

-- =========================
-- ROOMS
-- =========================
INSERT INTO room (name, type, price, available)
VALUES ('Room 101', 'STANDARD', 60.00, true),
       ('Room 102', 'STANDARD', 65.00, true),
       ('Room 103', 'STANDARD', 70.00, true),
       ('Room 201', 'DELUXE', 120.00, true),
       ('Room 202', 'DELUXE', 130.00, true),
       ('Room 203', 'DELUXE', 140.00, true),
       ('Suite 301', 'SUITE', 200.00, true),
       ('Suite 302', 'SUITE', 220.00, true),
       ('Penthouse', 'SUITE', 350.00, false),
       ('Budget Room', 'STANDARD', 50.00, true);

INSERT INTO booking (check_in_date, check_out_date, user_id, room_id)
VALUES ('2026-12-10', '2026-12-12', 1, 1),
       ('2026-12-15', '2026-12-18', 2, 2),
       ('2026-12-20', '2026-12-22', 3, 3),
       ('2026-12-28', '2027-01-02', 4, 7),
       ('2027-01-05', '2027-01-08', 5, 8),
       ('2027-01-15', '2027-01-18', 1, 4);