<html>
	<head>
		<meta context="text/html; charset=utf-8"/>
		<style type="text/css">
			div.mainFormClass {
				border: 2px solid #A0A0A0;
				padding: 10px 20px 0px 20px;
				width: 50%;
				margin-left: 25%;
				margin-right: 25%;
				height: auto;
				background-color: #F8F8F8;				
			}
			.title {
				font-size: 2em;
				text-align: center;
				font-style: italic;
				margin: 0px;
				padding: 0px;
			}
			.contents {
				height: auto;
				background-color: #F8F8F8;
				width: 60%;
				margin-left: 21%;
				margin-right: 19%;
				margin-top: 3%;
			}
			div.noRows {
				text-align:center;
				border-width:1px;
				border-style:solid;
				border-color:#A0A0A0;
				background-color:#E8E8E8;
				margin-bottom: 30px;
			}
			div.singleRow {
				text-align:center;
				border-width:1px;
				border-style:solid;
				border-color:#A0A0A0;
				background-color:#cccccc;
				margin-bottom: 30px;
			}
			div.formButtons{
				margin-left: 65px;
				padding-top: 3px;
			}
			div.tableElement {
				background-color: #E8E8E8;
			}
			table {
				margin: 0px;
				padding: 0px;
				height: auto;
				width: 100%;
				border-collapse: collapse;
				text-align: left;
			}
			td, th {
				border: 1px solid #A0A0A0;
			}
			a:hover {
				cursor: pointer;
			}
		</style>
		<script language="javascript">
				function changeVisibility() {
					var type = document.getElementsByName("searchType")[0];
					var value = type.options[type.selectedIndex].value;
					var placeInputs = document.getElementById("partially_hidden");
					if (value == "place") {
						placeInputs.style.visibility = 'visible';
					} else {
						placeInputs.style.visibility = 'hidden';
					}
				}

				function hideTableDisplay() {
					var tableDiv = document.getElementById("profileTable");
					if (null != tableDiv) {
						tableDiv.style.display = "none";
					}
					
					tableDiv = document.getElementById("albumsAndPostsGroup");
					if (null != tableDiv) {
						tableDiv.style.display = "none";
					}
				}

				function validateInput(form) {
					document.forms[0].getElementsByName();
				}

				function clearForm(form) {
					//var node = document.forms[0].getElementsByName('key')[0];
					//var form = document.forms[0];
					//form.reset();
					var formElements = form.elements;
					for (var i=0;i< formElements.length;i++) {
						var elem = formElements[i];
						if (elem.type == "text") {
							elem.value = "";
						} else if (elem.nodeName == "SELECT") {
							for(var j=0; j < elem.options.length; j++) {
							    if (elem.options[j].value == "user") {
							      elem.selectedIndex = j;
							      break;
							    }
							}
						}
						var placeInputs = document.getElementById("partially_hidden");
						placeInputs.style.visibility = 'hidden';
					}
					hideTableDisplay();
					document.getElementByID('searchKey').isvalid = true;
					//.resetForm();
				}

				function togglePostsDetailsTableView() {
					var postsTable = document.getElementById("postsTable");
					var albumsTable = document.getElementById("albumsTable");
					albumsTable.style.display = "none";
					postsTable.style.display = postsTable.style.display == "none" ? "inline" : "none";;
				}

				function toggleAlbumsDetailsTableView() {
					var albumsTable = document.getElementById("albumsTable");
					var postsTable = document.getElementById("postsTable");
					albumsTable.style.display = albumsTable.style.display == "none" ? "inline" : "none";
					postsTable.style.display = "none";
				}

				function toggleDisplayAlbumTableDiv(divId) {
					var div = document.getElementById(divId);
					div.style.display = div.style.display == "none" ? "inline" : "none";
				}

				function validateInput() {
					var inputKey = document.getElementById('searchKey');
					inputKey.oninvalid = function(e) {
											if(e.target.validity.valueMissing){
      											e.target.setCustomValidity('This cant be left empty');
      											return;
    										}
    										if(e.target.validity.patternMismatch){
      											e.target.setCustomValidity('& not allowed');
      											return;
    										}
    									};
					inputKey.oninput = function (e) {
										e.target.setCustomValidity('');
									};
				}
		</script>
		<title>Facebook search</title>
	</head>
	<body>
	<div class ="mainFormClass">
		<div class="title">
			Facebook Search
		</div>
		<HR />
		<form id="searchForm" name="searchForm" method="POST">
		<span >Keyword 
		<input id='searchKey' name="key" type="text" maxlength="100" size="35" required pattern='(?!.*&.*).+' value="<?php echo isset($_REQUEST['key']) ? $_REQUEST['key'] : '' ?>"/> </span></br>
		<div style='padding-top:3px;'>Type:	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<select name="searchType" onChange='changeVisibility();'>
			<option value="user" <?php if (isset($_REQUEST['searchType']) && $_REQUEST['searchType'] == 'user') echo 'selected="selected"'?>>Users</option>
			<option value="page" <?php if (isset($_REQUEST['searchType']) && $_REQUEST['searchType'] == 'page') echo 'selected="selected"'?>>Pages</option>
			<option value="event" <?php if (isset($_REQUEST['searchType']) && $_REQUEST['searchType'] == 'event') echo 'selected="selected"'?>>Events</option>
			<option value="group" <?php if (isset($_REQUEST['searchType']) && $_REQUEST['searchType'] == 'group') echo 'selected="selected"'?>>Groups</option>
			<option value="place" <?php if (isset($_REQUEST['searchType']) && $_REQUEST['searchType'] == 'place') echo 'selected="selected"'?>>Places</option>
		</select> </div>
		<div id="partially_hidden" style="padding-top:3px;<?php if (isset($_REQUEST['searchType']) && $_REQUEST['searchType'] == 'place') {echo 'visibility:visible;';} else {echo 'visibility:hidden;';} ?>">
			Location &nbsp;<input name="location" type="text" size="35" maxlength="100" value="<?php echo isset($_REQUEST['location']) ? $_REQUEST['location'] : '' ?>"/>
			Distance (meters) <input name="distance" type="text" size="35" maxlength="100" pattern='\d*' oninput = "setCustomValidity('');" oninvalid = "setCustomValidity(validity.valid ? '' :'Distance shall be a number');"value="<?php echo isset($_REQUEST['distance']) ? $_REQUEST['distance'] : '' ?>"/>
		</div>
		<div class="formButtons">
			<input type="submit" name="submit" value="Search"/>
			<input type="button" name="clear" value="Clear" onClick="clearForm(this.form);"/>
		</div>
		</form>
	</div>
	<?php
		function getSearchQuery(){
			$searchType = $_POST["searchType"];
			$query = '/search?q='.$_POST["key"]."&type=".$_POST["searchType"];
			if ($searchType == 'place') {
				$location = $_POST['location'];
				if (isset($location) && !empty($location)) {
					$geodata = file_get_contents('https://maps.googleapis.com/maps/api/geocode/json?address='.$location.'&key=AIzaSyAqIcH6L4JzZpTXVBKcLHFfLJBpAFcaLpE');
					$geojson = json_decode($geodata, true);
					$lat = $geojson['results'][0]['geometry']['location']['lat'];
					$long = $geojson['results'][0]['geometry']['location']['lng'];
					$query = $query.'&center='.$lat.','.$long;
					$distance = $_POST['distance'];
					if (!empty($distance)) {
						$query = $query.'&distance='.$distance;
					}
				}
			} 
			if ($searchType=='event') {
				$query = $query."&fields=id,name,picture.width(700).height(700),place&summary=true";
			} else {
				$query = $query."&fields=id,name,picture.width(700).height(700)&summary=true"; 
			}
			return $query;
		}

		function getDetailsQuery($id) {
			$query = "/".$id."?fields=id,name,picture.width(700).height(700),albums.limit(5){name,photos.limit(2){name, picture}},posts.limit(5)";
			return $query;
		}
	?>
	<?php
		//session_start();
		
			date_default_timezone_set('UTC');
			require_once 'Facebook/autoload.php';
			$fb = new Facebook\Facebook([
			'app_id' => '396304287403004',
			'app_secret' => 'ab0f2e512b0e9cce6b325635a6ddd8e2',
			'default_graph_version' => 'v2.8',
			]);
			$fb->setDefaultAccessToken('EAAFob8hGCZCwBAImUzbfbtOPLFzLkLhOoMmHdhmFdKijiMSDXTSTNLH8OBMbuDZAKuKYX07beIFKjn6EO3RUReDT51Oj1SGqRYCRyZA1Efuk3QUjyAwq7mMzHUauTo8naXtVF7MZBKwZAHPFxZBT5s');
		if ($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST["submit"])) {
			$table = "<div id = 'profileTable' class='contents'>";
			try {
				$query = getSearchQuery();
				$res = $fb->get($query);
				$graphEdge = $res->getGraphEdge();

				if (count($graphEdge) == 0) {
					$table = $table."<div class='noRows'>No records have been found</div>";
				} else {
					$table = $table."<div class='tableElement'><table><tr><th>Profile Photo</th><th>Name</th><th>";
					if($_POST['searchType'] == 'event') {
							$table = $table."Place</th>";
					} else {
							$table = $table."Details</th>";								
					}
					foreach($graphEdge as $graphNode) {
						$arr = $graphNode->asArray();
						$url =$arr['picture']['url'];
						$table = $table."<tr><td><a href='".$url."' target='_blank' style='display: inline;position:left;'><img src='".$url."' height='30px' width='40px'/></a></td><td>".$arr['name']."</td><td>";
						if($_POST['searchType'] == 'event') {
							if (array_key_exists('place', $arr)) {
								$table = $table.$arr['place']['name']."</td></tr>";
							} else {
								$table = $table." </td></tr>";
							}
						} else {
							$table = $table."<a href='./search.php?id=".$arr['id']."&key=".$_REQUEST['key']."&searchType=".$_REQUEST['searchType']."&location=".$_REQUEST['location']."&distance=".$_REQUEST['distance']."' >Details</td></tr>";//onclick='showAlbumsAndPosts()'
						}
					}
					//echo $table;
					$table = $table."</table></div>";
				}
				$table = $table.'</div>';
				echo $table;
			} catch(Facebook\Exceptions\FacebookResponseException $e) {
			  	echo 'Graph returned an error: ' . $e->getMessage();
			  	exit;
			} catch(Facebook\Exceptions\FacebookSDKException $e) {
				echo 'Facebook SDK returned an error: ' . $e->getMessage();
				exit;
			}
		}
		else if (isset($_GET["id"]) && !empty($_GET["id"])) {
			$detailsRes = $fb->get(getDetailsQuery($_GET["id"]));
			$detailsGraphNode = $detailsRes->getGraphNode();
			$detailsArr = json_decode($detailsGraphNode, true);
			$albumsExist = false;
			$postsExist = false;
			if (array_key_exists('albums',$detailsArr)) {
				$albumsExist = true;
			}
			if (array_key_exists('posts',$detailsArr)) {
				$postsExist = true;
			}	
			$albumsAndPostsTable = "<div id='albumsAndPostsGroup'><div id='albumsGroup' class='contents'>";						
			if ($albumsExist == true) {
				$albums_contents = $detailsArr['albums'];
				if (count($albums_contents) == 0) {
					$albumsAndPostsTable = $albumsAndPostsTable."<div id='albumsHeading' class='noRows'>No Albums have been found</div>";
				} else {
					$albumsAndPostsTable = $albumsAndPostsTable."<div id='albumsHeading' class='singleRow'><a href='#' onclick='toggleAlbumsDetailsTableView()'>Albums</a></div><div style='display:none;' class='tableElement' id='albumsTable'><table>";
					$index = 0;
					foreach($albums_contents as $album) {
						if (array_key_exists('name',$album)) {
							$albumHeading = $album['name'];
						} else {
							$albumHeading = "Untitled Album";
						}
							if (array_key_exists('photos',$album)) {
								$albumPhotos = $album['photos'];
								$index1 = $index + 1;
								$index2 = $index + 2;
								$albumsAndPostsTable = $albumsAndPostsTable."<tr id='row".$index1."'><td colspan=6><a href='#' onClick=toggleDisplayAlbumTableDiv('row".$index2."')>".$albumHeading."</a></td></tr>";
								$albumsAndPostsTable = $albumsAndPostsTable."<tr style='display:none;' id='row".$index2."'><td width='100%'>";
								$photo_count = 0;
								foreach($albumPhotos as $aPhoto) {
									if ($photo_count >= 2) { 
										break;
									} else {
										$picture_url = $aPhoto['picture'];
										$picture_id = $aPhoto['id'];
										//echo $picture_id."</br>";
										if ($photo_count > 0) {
											$albumsAndPostsTable = $albumsAndPostsTable."&nbsp;&nbsp";
										}
										$query = '/'.$picture_id.'/picture?redirect=false';
										$res_image = $fb->get($query);
										$graphEdge = $res_image->getGraphNode();
										$arr1 = json_decode($graphEdge, true);
										$url_img = $picture_url;
										if (array_key_exists('url',$arr1)) {
											$url_img = $arr1['url'];
										}
										$albumsAndPostsTable = $albumsAndPostsTable."<a href='".$url_img."'' target='_blank' style='display: inline;position:left;'><img src='".$picture_url."' height='80px' width='80px'/></a>";
									}
									$photo_count = $photo_count + 1;
								}
								$albumsAndPostsTable = $albumsAndPostsTable."</td></tr></div>";
								$index = $index + 2;
							} else {
								$index = $index + 1;
								$albumsAndPostsTable = $albumsAndPostsTable."<tr id='row".$index."'><td>".$albumHeading."</td></tr>";
							}
					}
					$albumsAndPostsTable = $albumsAndPostsTable.'</table></div>';
				}
			} else {
				$albumsAndPostsTable = $albumsAndPostsTable."<div id='albumsHeading' class='noRows'>No Albums have been found</div>";
			}
			$albumsAndPostsTable = $albumsAndPostsTable."</div>"."<div id='postsGroup' class='contents'>";
			if ($postsExist == true) {
				$albumsAndPostsTable = $albumsAndPostsTable."<div id='postsHeading' class='singleRow'><a href='#' onclick='togglePostsDetailsTableView()'>Posts</a></div>"."<div  style='display:none;' class='tableElement' id='postsTable'><table><th>Message</th>";;
				$post_contents = $detailsArr['posts'];
								
				foreach ($post_contents as $value) {
					if (array_key_exists('message',$value)) {
						$albumsAndPostsTable = $albumsAndPostsTable.'<tr><td>'.$value['message'].'</td></tr>';
					}
				}
				$albumsAndPostsTable = $albumsAndPostsTable.'</table></div>';

			} else {
				$albumsAndPostsTable = $albumsAndPostsTable."<div id='postsHeading' class='noRows'>No Posts have been found</div>";
			}
			$albumsAndPostsTable = $albumsAndPostsTable."</div>"."</div>";
			echo $albumsAndPostsTable;
			//unset($_GET["id"]);
		}
	?>
	</div>
	</body>
</html>