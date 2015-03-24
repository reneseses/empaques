function displayDays(){
	if(turnSelected.length < 2){
		$("#dia").empty();
		for(var i=0; i < 5; i++)
			$("#dia").append("<option value='" + days[i] +"'>" + dias[days[i]] + "</option>" );
	}
	
	if(turnSelected.length == 2){
		$("#dia").empty();
		for(var i=5; i < 7; i++)
			$("#dia").append("<option value='" + days[i] +"'>" + dias[days[i]] + "</option>" );
	}
	
	if(turnSelected.length > 2){
		$("#dia").empty();
		for(var i=0; i < 7; i++)
			$("#dia").append("<option value='" + days[i] +"'>" + dias[days[i]] + "</option>" );
	}
}