$(function(){
	var dias	= ["Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"],
		meses	= ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
		bloques	= {},
		days	= {};

	$("#planillas").change(function(){
		getTurnos($(this).val());
	});

	$.get(appContext + "member/usuarios/profile", function(data){
		var planillas= data.planillas;

		bloques	= data.bloques;
		days	= data.dias;

		if(planillas.length != 0){
			for(var i=0; i< planillas.length; i++){
				var planilla= planillas[i],
					option	= $("<option>");

				option.val(planilla.id).text(planilla.fecha);

				$("#planillas").append(option);
			}

			$("#planillas").change();
		}else{
			$("#planillas").parent().hide();
			$(".no-planillas").show();
		}
	}).fail(function(e){
		console.log(e);
	})

	function getTurnos(id){
		$("#planillas").prop("disabled", true);
		$(".info-container").hide();
		$(".loading-indicator").show();
		$.get(appContext + "member/usuarios/getTurnos/" + id, function(turnos){
			ordenar(turnos);
			addTurnos(turnos);
			$.get(appContext + "member/solicitudes/get?planilla=" + id, function(solicitudes){
				addSolicitudes(solicitudes);
				$(".loading-indicator").fadeOut(150, function(){
					$(".info-container").show();
					$("#planillas").prop("disabled", false);
				});
			});
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

		if(array == null){
			$("<div class='alert alert-danger alert-xs' style='margin-bottom: 20px'>").append("No existe planilla para la fecha solicitada").appendTo("#turnos");
			return false;
		}
		if(array.length == 0){
			$("<div class='alert alert-info alert-xs' style='margin-bottom: 20px'>").append("Ud no tiene turnos esta semana").appendTo("#turnos");
			return false;
		}

		var table	= $("<table class='table table-compact table-hover table-bordered table-striped'>"),
			head	= $("<thead>"),
			body	= $("<tbody>");

		head.append($("<tr>")
				.append($("<th colspan='3'>").addClass('text-center').text("Turnos Asignados")));

		head.append($("<tr>")
				.append($("<th>").css("width", "30%").text("Dia"))
				.append($("<th>").css("width", "30%").text("Hora"))
				.append($("<th>").text("Fecha")));
		
		for(var i in array){
			var turno	= array[i],
				horas	= turno.hora,
				minutos	= turno.minuto;
			
			if(horas < 10)
				horas = "0" + horas.toString();
			if(minutos < 10)
				minutos = "0" + minutos.toString();
			
			body.append($("<tr>")
					.append($("<td>").text(dias[turno.dia - 1]))
					.append($("<td>").text(horas + ":" + minutos))
					.append($("<td>").text(turno.diaMes + " de " + meses[turno.mes])));
		}

		table.append(head)
			.append(body);

		$("#turnos").append(table);
		return true;
	}

	function addSolicitudes(array){
		$("#solicitudes").empty();
		$("#repechajes").empty();
		
		if(array.length == 0){
			$("<div class='alert alert-danger alert-xs' style='margin-bottom: 20px'>")
				.append("Ud no realizó su solicitud de turnos")
				.appendTo("#solicitud");
			
			return false;
		}
		
		$("#solicitudes").empty();

		var table	= $("<table class='table table-compact table-hover table-bordered table-striped'>"),
			head	= $("<thead>"),
			body	= $("<tbody>");

		head.append($("<tr>")
				.append($("<th colspan='3'>").addClass('text-center').text("Solicitudes")));

		head.append($("<tr>")
				.append($("<th>").text("Dia").css("width", "30%"))
				.append($("<th>").text("Inicio").css("width", "30%"))
				.append($("<th>").text("Fin")));

		for(var i=0; i< array[0].length; i++){
			var tr	= $("<tr>"),
				sol = array[0][i];

			tr.append($("<td>").text(days[sol.dia]))
				.append($("<td>").text(bloques[sol.inicio]))
				.append($("<td>").text(bloques[sol.fin]));
			
			body.append(tr);
		}
		table.append(head)
			.append(body);
		
		$("#solicitudes").append(table);
		
		if(array.length == 1){
			$("#repechajes").hide();
			return
		}
		
		$("#repechajes").empty();
		$("#repechajes").show();
		
		table	= $("<table class='table table-compact table-hover table-bordered table-striped'>");
		head	= $("<thead>");
		body	= $("<tbody>");

		head.append($("<tr>")
				.append($("<th colspan='2'>").addClass('text-center').text("Solicitudes de Repechaje")));

		head.append($("<tr>")
				.append($("<th>").text("Dia").css("width", "30%"))
				.append($("<th>").text("Turnos")));
		
		for(var i=0; i < array[1].length; i++){
			var tr	=$("<tr>"),
				sol = array[1][i];

			tr.append($("<td>").text(days[sol.dia]));
			
			var str= "";
			for(var j in sol.inicio){
				str= str + bloques[sol.inicio[j]]+ ", ";
			}

			tr.append($("<td>").text(str.substring(0,str.length - 2)));
			
			body.append(tr);
		}

		table.append(head)
			.append(body);
		
		$("#repechajes").append(table);
	}
})