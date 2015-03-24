var turnSelected=[];

$(function(){
	
	if(typeof repechaje != 'undefined'){
		turnSelected= repechaje;
		for(var i=0; i<repechaje.length; i++){
			var css		= "alert-success";
			var dia		= dias[repechaje[i].dia];
			var str		= "";
			var array	= repechaje[i].inicio;

			if(dia.toLowerCase() == "domingo" || dia.toLowerCase() == "sabado")
				css= "alert-info";

			for(var j=0; j< array.length; j++)
				str= str + horas[array[j]] + ", ";

			str= str.substring(0, str.length - 2);
			
			var newTurno = "<li class='alert "+ css + " alert-xs fade-in' id='turno" + i + "'>" +
				"<button class='close' id='"+ i + "'>×</button>"+
				"<label class='day-label'>" + dia +"</label>: " + str  + "</li>";


			$("#turnos").append(newTurno);
			$("#turnum").text("Turno " +  (turnSelected.length + 1));

			if(turnSelected.length > 2)
				$("#enviar").removeAttr("disabled","disabled");
			$(".errors").show();
		}
	}
	
	for(var i= 0; i < 7; i++)
		$("#dia").append("<option value='" + days[i] +"'>" + dias[days[i]] + "</option>" );
	
	$("#dia").change(function(){
		index= $("#dia").prop("selectedIndex");
		$("#begin").empty();
		for(var i = 0; i < turnos[index].length; i++){
			$("#begin").append("<option value='" + buscarBloque(horas, turnos[index][i]) +"'>" + turnos[index][i] + "</option>" );
		};
	});

	$("#elegir").click(function(){
		if(turnSelected.length == 5)
			return;
		
		dia = $("#dia option:selected").text();
		if(dia==""){
			$("#dia").focus();
			alert("Dia no seleccionado");
			return;
		}
		inicio= "";
		arrayTurnos=[];
		$("#begin option:selected").each(function(){
			inicio= inicio + $(this).text() + ", ";
			arrayTurnos.push($(this).val());
		});
		inicio = inicio.substring(0, inicio.length - 2);
		if(inicio==""){
			$("#begin").focus();
			alert("Hora no seleccionada");
			return;
		}
		
		var turno = {dia: $("#dia option:selected").val(), inicio: arrayTurnos};
		if(buscarConflicto(turno)){
			alert("Hay una solicitud en conflicto. No se pueden repetir los dias");
			return;
		}

		var css= "alert-success";

		if(dia.toLowerCase() == "domingo" || dia.toLowerCase() == "sabado")
			css= "alert-info";

		var newTurno = "<li class='alert "+ css + " alert-xs fade-in'>" +
				"<button class='close' id='"+ turnSelected.length + "'>×</button>"+
				"<label class='day-label'>" + dia +"</label>: " + inicio  + "</li>";

		$("#turnos").append(newTurno);
		turnSelected.push(turno);
		$("#turnum").text("Turno " +  (turnSelected.length + 1));
		if(turnSelected.length == 1)
			$("#enviar").removeAttr("disabled");
	});
	
	$("#turnos").on("click", ".alert button.close", function(){
		turnoNum= $(this).attr("id");
		turnSelected.splice(turnoNum, 1);
		$("#turnum").text("Turno " +  (turnSelected.length + 1));
		$(this).parent().remove();

		$("#turnos").children().each(function(index){
			var id= $(this).children("button").attr("id");
			$(this).children().attr("id", index);
		});
	});
	
	$("#enviar").click(function(){
		var data= {turnos: JSON.stringify(turnSelected)};
		if(typeof repechaje != 'undefined')
			data= {turnos: JSON.stringify(turnSelected), id: repechajeId};

		$.post(recibir_url, data, function(){
			window.location = parent_url + "gracias";
		}).fail(function(e){
			console.log(e);
		});
	});
	
	function buscarConflicto(turno){
		for(var i in turnSelected){
			if(turnSelected[i].dia == turno.dia)
				return true;
		}
		return false;
	}
	function buscarBloque(object, value){
		for(prop in object){
			if(object[prop] == value)
				return prop;
		}
		return "";
	}
	
});