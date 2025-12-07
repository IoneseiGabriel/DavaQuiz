INSERT INTO game (
    id,
    title,
    description,
    status,
    created_by,
    created_at,
    updated_at
)
VALUES (
           1,
           'General Knowledge Quiz',
           'A simple quiz covering random facts.',
           'DRAFT',
           200,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );


INSERT INTO question (
    id, game_id, text, image_url, options, correct_option_index
)
VALUES (
        1,
        1,
        'What is the capital of France?',
        NULL,
        '["Paris", "London", "Berlin", "Rome"]',
        0
);
