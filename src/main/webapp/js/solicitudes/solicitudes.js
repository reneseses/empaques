var turnSelected=[];

$(function(){
	if(solicitud){
		turnSelected= solicitud;
		for(var i=0; i<solicitud.length; i++){
			var css= "alert-success";
			var dia= dias[solicitud[i].dia];

			if(dia.toLowerCase() == "domingo" || dia.toLowerCase() == "sabado")
				css= "alert-info";

			newTurno = "<li class='alert "+ css + " alert-xs fade-in' id='turno" + i + "'>" +
				"<button class='close' id='"+ i + "'>×</button>"+
				"<label class='day-label'>" + dia +"</label>: " + horas[solicitud[i].inicio] + " - " + horas[solicitud[i].fin]  + "</li>";

			$("#turnos").append(newTurno);
			$("#turnum").text("Turno " +  (turnSelected.length + 1));
			if(turnSelected.length > 2)
				$("#enviar").removeAttr("disabled","disabled");
			$(".errors").show();
		}
	}
	
	displayDays();
	
	$("#begin").change(function(){
		index = $("#begin").prop("selectedIndex");
		$("#end").empty();
		index= index + 3;
		if(bloques.length - index < 1)
			index=bloques.length - 1;
		for(var i = index; i <  bloques.length; i++){
			$("#end").append("<option value='" + bloques[i] +"'>" + horas[bloques[i]] + "</option>" );
		}
	});
	$("#elegir").click(function(){
		if(turnSelected.length == 5)
			return;
		
		if(turnSelected.length == 2)
			$("#enviar").removeAttr("disabled","disabled");
		
		dia = $("#dia option:selected").text();
		if(dia==""){
			$("#dia").focus();
			alert("Dia no seleccionado");
			return;
		}
		inicio = $("#begin option:selected").text();
		if(inicio==""){
			$("#begin").focus();
			alert("Hora de inicio no seleccionada");
			return;
		}
		fin = $("#end option:selected").text();
		if(fin==""){
			$("#end").focus();
			alert("Hora de fin no seleccionada");
			return;
		}
		var turno = {dia: $("#dia option:selected").val(), inicio: $("#begin option:selected").val(), fin: $("#end option:selected").val()};

		if(buscarConflicto(turno)){
			alert("Hay un turno en conflicto. Debe haber como minimo 3:30 horas entre cada turno");
			return;
		}

		var css= "alert-success";
		if(dia.toLowerCase() == "domingo" || dia.toLowerCase() == "sabado")
			css= "alert-info"

		newTurno = "<li class='alert "+ css + " alert-xs fade-in' id='turno" + turnSelected.length + "'>" +
			"<button class='close' id='"+ turnSelected.length + "'>×</button>"+
					"<label class='day-label'>" + dia +"</label>: " + inicio + " - " + fin  + "</li>";
		$("#turnos").append(newTurno);
		turnSelected.push(turno);
		$("#turnum").text("Turno " +  (turnSelected.length + 1));
		$("#begin option:selected").removeAttr("selected");
		$("#end").empty();
		displayDays();
	});

	$("#turnos").on("click", ".alert button.close", function(){
		turnoNum= $(this).attr("id");
		
		if(turnoNum < 3){
			turnSelected.splice(turnoNum, turnSelected.length - turnoNum);
			length = $("#turnos").children().length;
			for(var i = turnoNum; i < length; i++){
				$("#" + i).parent().remove();
			}
		}
		else{
			turnSelected.splice(turnoNum, 1);
			$(this).parent().remove();
			for(i = turnoNum; i <= $("#turnos").children().length; i++){
				$("#" + i).parent().attr("id", "turno" +  (i - 1));
				$("#" + i).attr("id", i - 1);
			}
		}    
		displayDays();
		if(turnSelected.length < 3)
			$("#enviar").attr("disabled","disabled");
		$("#turnum").text("Turno " +  (turnSelected.length + 1));
	});
	
	$("#enviar").click(function(){
		var data= {turnos: JSON.stringify(turnSelected)};
		
		if(solicitud)
			data= {turnos: JSON.stringify(turnSelected), id: solicitudId};

		$.post(url, data, function(){
			window.location = parent_url + "gracias";
		}).fail(function(e){
			console.log(e);
			alert("Ha ocurrido un error");
		});
	});
});

function buscarConflicto(turno){
	for(var i in turnSelected){
		if(turnSelected[i].dia != turno.dia)
			continue;
		var inicio = getIndex(turnSelected[i].inicio);
		if(getIndex(turno.inicio) - inicio < 7 && getIndex(turno.inicio) - inicio >= 0){
			return true;
		}
		if(inicio - getIndex(turno.inicio) < 7 && inicio - getIndex(turno.inicio) >= 0){
			return true;
		}
	}
	return false;
}

function getIndex(hora){
	for(var i=0; i< bloques.length; i++){
		if(hora == bloques[i])
			return i;
	}
}