$(function(){
	var dias = ["Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"];
	var meses = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];

	for(var i=0; i < planillas.length; i++){
		var date = new Date(planillas[i].fecha);
		if(planillas[i].id == id){
			$("#planillas").append($("<option>").attr("selected", "selected").val(planillas[i].id).text(date.getDate() + " de " + meses[date.getMonth()] + ", " + date.getFullYear()));
			getTurnos(id);
		}
		else
			$("#planillas").append($("<option>").val(planillas[i].id).text(date.getDate() + " de " + meses[date.getMonth()] + ", " + date.getFullYear()));
	}
	
	$("#planillas").change(function(){
		getTurnos($(this).val());
	});
	
	function getTurnos(id){
		$("#planillas").prop("disabled", true);
		$(".info-container").hide();
		$(".loading-indicator").show();
		$.ajax({
			url: appContext + "member/usuarios/getTurnos/" + id,
			dataType: 'json',
			success: function(data){
				ordenar(data);
				addTurnos(data);
				$.ajax({
					url: appContext + "member/solicitudes/get?planilla=" + id,
					dataType: 'json',
					success: function(data){
						addSolicitudes(data);
						$(".loading-indicator").fadeOut(150, function(){
							$(".info-container").show();
							$("#planillas").prop("disabled", false);
						});
					}
				});
			}
		});
	}

	function ordenar(arreglo){
		for(var i = 0; i < arreglo.length - 1; i++){
			menor = i;
			for(var j = i + 1; j < arreglo.length; j++){
				if(arreglo[menor].mes > arreglo[j].mes)
					menor = j;
				else
					if(arreglo[menor].diaMes > arreglo[j].diaMes)
						menor = j;
			}
			aux = arreglo[i];
			arreglo[i] = arreglo[menor];
			arreglo[menor] = aux;
		}
		return arreglo;
	}

	function addTurnos(array){
		$("#turnos").empty();
		$("#turnos").append($("<h4>").append("Mis Turnos").css("margin-top", "0"));
		if(array == null){
			$("<div class='alert alert-danger alert-xs' style='margin-bottom: 20px'>").append("No existe planilla para la fecha solicitada").appendTo("#turnos");
			return false;
		}
		if(array.length == 0){
			$("<div class='alert alert-info alert-xs' style='margin-bottom: 20px'>").append("Ud no tiene turnos esta semana").appendTo("#turnos");
			return false;
		}
		var table = $("<table class='table table-compact table-hover table-bordered table-striped'>");
		var head= $("<thead>");
		head.append($("<tr>").append($("<th>").css("width", "30%").append("Dia")).append($("<th>").css("width", "30%").append("Hora")).append($("<th>").append("Fecha")));
		var body = $("<tbody>");
		for(var i in array){
			var turno = array[i];
			var horas = turno.hora;
			var minutos = turno.minuto;
			if(horas < 10)
				horas = "0" + horas.toString();
			if(minutos < 10)
				minutos = "0" + minutos.toString();
			body.append($("<tr>").append($("<td>").append(dias[turno.dia - 1])).append($("<td>").append(horas + ":" + minutos)).append($("<td>").append(turno.diaMes + " de " + meses[turno.mes])));
		}
		table.append(head).append(body);
		$("#turnos").append(table);
		return true;
	}
	function addSolicitudes(array){
		$("#solicitudes").empty();
		$("#repechajes").empty();
		if(array.length == 0){
			$("<div class='alert alert-danger alert-xs' style='margin-bottom: 20px'>").append("Ud no realizó su solicitud de turnos").appendTo("#solicitud");
			return false;
		}
		$("#solicitudes").empty();
		$("#solicitudes").append($("<h4>").append("Solicitud").css("margin-top", "0"));
		var table = $("<table class='table table-compact table-hover table-bordered table-striped'>");
		var head= $("<thead>");
		head.append($("<tr>").append($("<th>").append("Dia").css("width", "30%")).append($("<th>").append("Inicio").css("width", "30%")).append($("<th>").append("Fin")));
		var body = $("<tbody>");
		for(var i in array[0]){
			var tr=$("<tr>");
			var sol = array[0][i];
			tr.append($("<td>").append(days[sol.dia])).append($("<td>").append(bloques[sol.inicio])).append($("<td>").append(bloques[sol.fin]));
			body.append(tr);
		}
		table.append(head).append(body);
		$("#solicitudes").append(table);
		
		if(array.length == 1){
			$("#repechajes").hide();
			return
		}
		$("#repechajes").empty();
		$("#repechajes").show();
		$("#repechajes").append($("<h4>").append("Repechaje").css("margin-top", "0"));
		var head= $("<thead>");
		head.append($("<tr>").append($("<th>").append("Dia").css("width", "30%")).append($("<th>").append("Turnos")));
		var table = $("<table class='table table-compact table-hover table-bordered table-striped'>");
		var body = $("<tbody>");
		for(var i in array[1]){
			var tr=$("<tr>");
			var sol = array[1][i];
			tr.append($("<td>").append(days[sol.dia]));
			var str= "";
			for(var j in sol.inicio){
				str= str + bloques[sol.inicio[j]]+ ", ";
			}
			tr.append($("<td>").append(str.substring(0,str.length - 2)));
			body.append(tr);
		}
		table.append(head).append(body);
		$("#repechajes").append(table);
	}
})