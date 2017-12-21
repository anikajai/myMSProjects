var app = angular.module('yoApp',[]);
window.alert("we are here");
app.controller('myController', function($scope, $http, $window) {
	 $scope.showPosition = function(position) {
		var coordinates = position.coords;
		$scope.latitude = coordinates.latitude; 
		$scope.longitude = coordinates.longitude; 
	}
	if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    }
    $scope.type="user";
    $window.alert("we are here");
	$scope.submit = function() {
		$http({
			method: 'GET',
			url: 'search.php',
			params: {"key": $scope.keyword, "searchType":"user"}
		}).then (function successCallback(response){
					$scope.userData = response.data.data;
					$scope.userPaging = $scope.getPaging(response.data.paging);
					$http({
						method: 'GET',
						url: 'search.php',
						params: {"key": $scope.keyword, "searchType":"page"}
					}).then (function successCallback(response){
								$scope.pageData = response.data.data;
								$scope.pagePaging = $scope.getPaging(response.data.paging);
								$http({
									method: 'GET',
									url: 'search.php',
									params: {"key": $scope.keyword, "searchType":"event"}
								}).then (function successCallback(response){
											$scope.eventData = response.data.data;
											$scope.eventPaging = $scope.getPaging(response.data.paging);
											$http({
												method: 'GET',
												url: 'search.php',
												params: {"key": $scope.keyword, "searchType":"place", "latitude":$scope.latitude, "longitude":$scope.longitude}
											}).then (function successCallback(response){
														$scope.placeData = response.data.data;
														$scope.placePaging = $scope.getPaging(response.data.paging);
														$http({
															method: 'GET',
															url: 'search.php',
															params: {"key": $scope.keyword, "searchType":"group"}
														}).then (function successCallback(response){
																	$scope.groupData = response.data.data;
																	$scope.groupPaging = $scope.getPaging(response.data.paging);
																}, function errorCallback(response) {
													    				// called asynchronously if an error occurs
													    				// or server returns response with an error status.
													  			});
													}, function errorCallback(response) {
									    				// called asynchronously if an error occurs
									    				// or server returns response with an error status.
									  				});
										}, function errorCallback(response) {
							    				// called asynchronously if an error occurs
							    				// or server returns response with an error status.
							  			});
							}, function errorCallback(response) {
				    				// called asynchronously if an error occurs
				    				// or server returns response with an error status.
	  						});
				}, function errorCallback(response) {
	    				// called asynchronously if an error occurs
	    				// or server returns response with an error status.
	  			});
	}

	$scope getPaging = function (paging) {
		// body...
	}

	$scope isSelectedTab = function(type) {
		if ($scope.selectedTab == type) {
			return true;
		} else {
			return false;
		}
	}

	$scope selectTab = function(type) {
		$scope.selectedTab = type;
	}

	$scope getSelectedTab = function() {
		return $scope.selectedTab;
	}

});