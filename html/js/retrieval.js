function get(){
	var person = document.getElementById('person').value;
	var attribute = document.getElementById('attribute').value;
	return person + ' ' + attribute;
}


function retrieval_method(){
	//var searchresult = getSearchResult(searchkey);
	//return searchresult;
	document.getElementById('retrieval_results').innerHTML = get();     
}
