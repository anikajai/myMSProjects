The files users.dat, ratings.dat and movies.dat are needed for the tasks. The description of the data is provided in the README file.

Task1.Python
=================
Task is to calculate each movie’s average rating based on gender of the user. The ratings.dat and users.dat file are needed for this task.
It accepts two arguments: Full file path for users.dat and ratings.dat respectively.
	Description
	==============
	1. usersFile_rdd contains the the tuple of (userId, gender) as key value pair after separating words in each line with "::" delimeter od users.dat file.
	2. ratingUserFile_rdd contains the the tuple of (userID, (movieID, rating)) as key value pair after separating words in each line with "::" delimeter of ratings.dat file.
	3. Then joined these two on the equality of userID. Then mapped each key as (movieId, gender) and value as (rating,1). Then computed the average per key.

Task2.Python
=================
Task is to calculate the average rating of each movie genres based on the gender of the user. The movies.dat, ratings.dat and users.dat file are needed for this task.
It accepts three arguments: Full file path for users.dat, ratings.dat and movies.dat respectively.
	Description
	==============
	1. usersFile_rdd contains the the tuple of (userId, gender) as key value pair after separating words in each line with "::" delimeter od users.dat file.
	2. ratingUserFile_rdd contains the the tuple of (userID, (movieID, rating)) as key value pair after separating words in each line with "::" delimeter of ratings.dat file.
	3. moviesFile_rdd contains the the tuple of (movieID, genres) as key value pair after separating words in each line with "::" delimeter of movies.dat file.
	4. Joined usersFile_rdd and ratingUserFile_rdd on the equality of userID into user_rating_joined. Then mapped each item as a key value pair of (movieId, (gender, rating).
	5. Joined user_rating_joined and moviesFile_rdd on the equality of movieID. Then mapped each key as (genre, gender) and value as (rating,1). Then computed the average per key.
