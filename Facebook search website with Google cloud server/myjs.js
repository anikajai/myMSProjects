//window.alert("i am  are nowhere");
var app = angular.module('app1',[]);
app.controller('myController', function($scope, $http, $window) {
								if ($window.navigator.geolocation) {
							        $window.navigator.geolocation.getCurrentPosition(function(position) {
																	var coordinates = position.coords;
																	$scope.latitude = coordinates.latitude; 
																	$scope.longitude = coordinates.longitude; 
																});
							    }
							    $scope.selectedTab = "user";
							    //$window.alert("we are here" + $scope.selectedTab);

							    $scope.submit = function() {
							    					$http({
														method: 'GET',
														url: 'search.php',
														params: {"key": $scope.keyword, "searchType":"user"}
													}).then (function successCallback(response){
																$scope.userData = response.data.data;
																$scope.userPaging = response.data.paging.next;
																window.alert("hey");
															}, function errorCallback(response) {
												    				// called asynchronously if an error occurs
												    				// or server returns response with an error status.
												    				window.alert("no");
												  			});
													$window.alert("Length is:" + $scope.userData);
												}
							    $scope.getPaging = function (paging) {
									// body...
								}

								$scope.isSelectedTab = function(type) {
									if ($scope.selectedTab == type) {
										return true;
									} else {
										return false;
									}
								}

								$scope.selectTab = function(type) {
									$scope.selectedTab = type;
								}

								$scope.getSelectedTab = function() {
									return $scope.selectedTab;
								}       							
			});