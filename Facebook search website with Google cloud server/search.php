	<?php
		 function get_myurl_data($url) {
				$ch = curl_init();
				$timeout = 5;
				curl_setopt($ch, CURLOPT_URL, $url);
				curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
				curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, $timeout);
				$data = curl_exec($ch);
				curl_close($ch);
				return $data;
		}
		$searchType = $_GET["searchType"];
		$key = $_GET["key"];
		$myJson = "";
		if ($searchType == 'place') {
			$latitude = $_GET["latitude"];
			$longitude = $_GET["longitude"];
			$json_url = "https://graph.facebook.com/v2.8/search?q=".$key."&type=".$searchType."&center=".$latitude.",".$longitude."&fields=id,name,picture.width(700).height(700)&access_token=EAACEdEose0cBAIH3iHmQ8ecE56X4lzHtANNbfZBKChbdLrZA7LqnQE3iLI5G44q5cEZB50j7A7qO0YiV2UZCkfiIcMT933l9gGm8rBMlZCODm5h1eKlvbQig3ElebZASRR9ZCxv7wZB2VMm7zMu2VLxeRnsHg9xjotfrFpEobAZAKBwPnU9ZB24CXkSXJtIPYaFgsZD"
			$myJson = get_myurl_data($json_url);
		} else {
			$json_url = "https://graph.facebook.com/v2.8/search?q=".$key."&type=".$searchType."&fields=id,name,picture.width(700).height(700)&access_token=EAACEdEose0cBAIH3iHmQ8ecE56X4lzHtANNbfZBKChbdLrZA7LqnQE3iLI5G44q5cEZB50j7A7qO0YiV2UZCkfiIcMT933l9gGm8rBMlZCODm5h1eKlvbQig3ElebZASRR9ZCxv7wZB2VMm7zMu2VLxeRnsHg9xjotfrFpEobAZAKBwPnU9ZB24CXkSXJtIPYaFgsZD"
			$myJson = get_myurl_data($json_url);
		}
		echo $myJson;
	?>