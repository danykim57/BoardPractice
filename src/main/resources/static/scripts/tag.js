document.addEventListener("DOMContentLoaded", function(){

 // Handler when the DOM is fully loaded
  $("#selectBox").append('<option value="option5">option5</option>');
  $("#selectBox").append('<option value="option7" selected>option7</option>');
});


var input = document.querySelector('input[name=tags]')

new Tagify(input)

