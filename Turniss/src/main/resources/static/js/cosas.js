$(document).ready(function(){
    $("#selectorEspecialidad").click(function(){
    	$(".especialidad").addClass('hidden')  
    	var espec = $( "#selectorEspecialidad" ).val();
    	$("." + espec).removeClass('hidden');  
    		//alert( espec );
    });
});

$(document).ready(function(){
    $("#loginNavVar").click(function(){
        $("#loginNavVarContent").slideDown("slow");
    });
});


/*
$(document).ready(function() {
    $("#selectorMedico").children('option:gt(0)').hide();
    $("#selectorEspecialidad").change(function() {
        $("#selectorMedico").children('option').hide();
        $("#selectorMedico").children("option[class=" + $(this).val() + "]").show();
    })
})
*/

//$("option_you_want_to_hide").wrap('<span/>') 
//$("option_you_want_to_show").unwrap();
//$(this)  es un select
//var especialidad = akjhdkjasdkajsd;
//$("#mi-select option."+especialidad).show();
