CREATE TABLE public.ikea_tweets (
                                    id varchar NOT NULL,
                                    "text" varchar NULL,
                                    created_at timestamp with time zone NULL,
                                    "language" varchar NULL,
                                    CONSTRAINT ikea_tweets_pk PRIMARY KEY (id)
);
