var changes= [];
var grid;

function negativeValueRenderer(instance, td, row, col, prop, value, cellProperties) {
	Handsontable.TextCell.renderer.apply(this, arguments);
	$(td).css({
		background: '#fff',
		width: 105,
		textAlign: "right"
	});
	
	if(value === '_') {
		td.style.background = '#eee';
		td.style.color= "#eee"
	}
	else if(value === '-') {
		td.style.background = '#c1e2b3';
		td.style.color= "#c1e2b3";
		td.style.textAlign = "center";
	}
	/*else if(value === "_"){
		td.style.background = '#f0f0f0';
		td.style.textAlign = "center";
	}*/
}
var columns;

if(readonly){
	columns = [
		{data: "Lunes", readOnly: true},
		{data: "Martes", readOnly: true},
		{data: "Miercoles", readOnly: true},
		{data: "Jueves", readOnly: true},
		{data: "Viernes", readOnly: true},
		{data: "Sabado", readOnly: true},
		{data: "Domingo", readOnly: true},
	];
}

else{
	columns = [
		{data: "Lunes"},
		{data: "Martes"},
		{data: "Miercoles"},
		{data: "Jueves"},
		{data: "Viernes"},
		{data: "Sabado"},
		{data: "Domingo"},
	];
}

$(function () {
	var startDate	= date,
		endDate		= date;
	
	var curr_date	= date.getDate(),
		curr_month	= date.getMonth() + 1,
		curr_year	= date.getFullYear();

	if(curr_date <10)
		curr_date= "0" + curr_date;
	 if(curr_month <10)
	 	curr_month= "0" + curr_month;
	
	//$('.week-picker').val(curr_date + "-" + curr_month + "-" + curr_year);

	var height	= $(window).height() - $("#planilla").position().top - 21,
		width	= $("#planilla").width();

	$("#planilla").parent().height(height).css({overflow: "hidden"});
	
	var colWidth= Math.floor(width/7);

	var lastWidth= width - colWidth* 6;
	//lastWidth= lastWidth> 80? lastWidth: 80;
	$.ajax({
		url: dataUrl,
		dataType: 'json',
		success: function(data) {
			var container	= document.getElementById('planilla');
				rowHeaders	= data[0],
				tableData	= data[1],
				colHeaders		= ["Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"];

			grid= new Handsontable(container, {
				data		: tableData,
				colHeaders	: colHeaders,
				rowHeaders	: rowHeaders,
				columns		: columns,
				stretchH	: 'all',
				maxRows		: tableData.length,
				cells		: function (row, col, prop) {
					var cellProperties= {};
					if(this.instance.getData()[row][prop] == '_')
						cellProperties.readOnly=true;
					
					cellProperties.renderer= negativeValueRenderer;

					return cellProperties;
				},
			});

			grid.addHook('afterChange', function(change){
				getChanges(change, grid.getData());
			});

			$(".handsontable table").addClass("table table-bordered table-striped table-hover");
		}
	});
	

	var selectCurrentWeek = function() {
		window.setTimeout(function () {
			$('.xdsoft_datetimepicker').find('.xdsoft_current').parent().addClass('xdsoft_row_current');
		}, 40);
	};

	$('.week-picker').keydown(function(e){
		e.preventDefault();
		return;
	}).datetimepicker({
		format      	: 'Y-m-d 00:00',
		timepicker		: false,
		dayOfWeekStart	: 1,
		format			: 'd-m-Y',
		value			: moment(date).format("DD-MM-YYYY"),
		onShow:function( ct ){
			selectCurrentWeek();
		},
		onSelectDate: function(ct){
			var mom= moment(ct).startOf("week").add(1, "d");
			var d= mom.toDate();
			this.setOptions({
				value: moment(d).format("DD-MM-YYYY")
			});

			selectCurrentWeek();
		},
	});

	$('body').on('mouseover', '.xdsoft_datetimepicker .xdsoft_calendar tbody tr', function() { $(this).addClass('xdsoft_row_hover'); });
	$('body').on('mouseout','.xdsoft_datetimepicker .xdsoft_calendar tbody tr', function() { $(this).removeClass('xdsoft_row_hover'); });
});

function save(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(grid.getData()), fecha: $(".week-picker").val()},
		type: "POST",
		success:function(data){
			window.location.href = parent + "member/planillas";
		}
	});
}

function updateTurnos(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(changes), id: planillaId},
		type: "POST",
		success:function(){
			window.location.href = parent + "member/planillas";
		}
	});
}

function update(){
	$.ajax({
		url: saveUrl,
		data: {data: JSON.stringify(changes), id: planillaId},
		type: "POST",
		success:function(){
			window.location.href = parent + "member/planillas";
		}
	});
}
