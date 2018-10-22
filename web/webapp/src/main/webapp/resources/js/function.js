var url = "http://localhost:8080/webapp/";

function send() {
  $('#result').html("Sending...");
  var users = $('input:checkbox:checked').map(function () {
    return this.value;
  }).get();
  var subject = $('#subject').val();
  var body = $('#body').val();

  $.ajax({
    url: url,
    type: "POST",
    data: "users=" + users + "&subject=" + subject + "&body=" + body,
    dataType: "html",
    success: function (data) {
      $('#result').html(data);
    }
  });
}