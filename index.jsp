<html>
<body>
    <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
            <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
                <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
                <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<style>
    .wrapper {
        text-align: center;
        
    }



</style>

<div class="wrapper">
<img src="http://www.marotura.com/wp-content/uploads/2014/02/search_google_2.png" alt="Mountain View" width=500 height:500 >
  </div>
<form action="result.jsp" method="POST">
<div class="wrapper">
<input type="text" id="texts" name="customsearch" value="">
 </div>
<div class="wrapper">
<button class="btn btn-success" onclick="myFunction()">Search</button>
 </div>

</form>
<script>
function myFunction() {
  
    var result=document.getElementById("texts").value;
    var ary=result.split(" ");
    var first=ary[0];
    var second= ary[1];
    //alert(first);
    // query the servlet for the information requested
    var xmlHttp = new XMLHttpRequest();
     xmlHttp.open( "POST", "ServletTest", false );
     var params = "firstWord=" + first + "&secondWord="+ second;
     xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
     xmlHttp.send( params );
     //alert("http");
     var responseText = xmlHttp.responseText;
     
     
    
    
    

}
</script>

</body>
</html>



