function displayDays(){
	if(turnSelected.length < 5){
		$("#dia").empty();
		for(var i=0; i < 5; i++)
			$("#dia").append("<option value='" + days[i] +"'>" + dias[days[i]] + "</option>" );
	}
}