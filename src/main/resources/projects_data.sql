INSERT INTO projects (project_name, estimated_hours, actual_hours, difficulty, notes)
VALUES ("Build a treehouse", 8, 9, 4, "Be careful when waterproofing, especially the roof."), 
("Hang a door", 4, 3, 3, "Use the door hangers from Home Depot.");

INSERT INTO material (project_id, material_name, num_required, cost)
VALUES (1, "hardwood boards", 36, 55.60),
(1, "cans of sealant", 2, 7.26),
(1, "rope coils", 2, 4.50).
(1, "nails", 72, 2.25),
(2, "door in frame", 1, 750),
(2, "2-inch screws", 20, 1.45),
(2, "door hangers", 4, 8.73);

INSERT INTO step (project_id, step_text, step_order)
VALUES (1, "Build frame in a sturdy spot on the tree.", 1),
(1, "Lay down and waterproof roof.", 2),
(1, "Finish walls and floor.", 3),
(1, "Assemble and attach ladder.", 4),
(1, "Decorate!", 5),
(2, "Screw door hangers on the top and bottom of each side of the door.", 1),
(2, "Attach door to door hangers.", 2);

INSERT INTO category (category_name)
VALUES ("Outdoor"),
("Doors and windows");

INSERT INTO project_category (project_id, category_id)
VALUES (1, 1),
(2, 2);