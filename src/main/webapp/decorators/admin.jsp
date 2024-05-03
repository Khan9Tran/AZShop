<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%>
<!doctype html>
<html lang="en" data-bs-theme="dark">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Security-Policy" content="">

<title><decorator:title></decorator:title></title>

<!--plugins-->
<link rel="stylesheet"
	href="<c:url value="/templates/assets/plugins/perfect-scrollbar/css/perfect-scrollbar.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/plugins/metismenu/css/metisMenu.min.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/plugins/simplebar/css/simplebar.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/plugins/fancy-file-uploader/fancy_fileupload.css"></c:url>"
	type="text/css">
<!-- loader-->
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/pace.min.css"></c:url>"
	type="text/css">
<script
		src="<c:url value="/templates/assets/js/pace.min.js"></c:url>"></script>
<link rel="stylesheet"
	href="<c:url value="/templates/assets/plugins/datatable/css/dataTables.bootstrap5.min.css"></c:url>"
	type="text/css">
<!--Styles-->
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/bootstrap.min.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/bootstrap.min.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/icons.css"></c:url>"
	type="text/css">
<link
	href="https://fonts.googleapis.com/css2?family=Noto+Sans:wght@300;400;500;600&display=swap"
	rel="stylesheet">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/main.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/dark-theme.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/semi-dark-theme.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/minimal-theme.css"></c:url>"
	type="text/css">
<link rel="stylesheet"
	href="<c:url value="/templates/assets/css/shadow-theme.css"></c:url>"
	type="text/css">

</head>
<body>
	<%@ include file="/common/admin/header.jsp"%>
 	<%@ include file="/common/admin/sidebar.jsp"%> 

	<!--start main content-->
	<decorator:body></decorator:body>
	<main class="page-content"></main>
	<!--end main content-->

	<!--start overlay-->
	<div class="overlay btn-toggle-menu"></div>
	<!--end overlay-->

	<%@ include file="/common/vendor/search.jsp"%>
	<%@ include file="/common/vendor/theme.jsp"%>


	<!--plugins-->
	<script
		src="<c:url value="/templates/assets/js/jquery.min.js"></c:url>"></script>
	<script
		src="<c:url value="/templates/assets/plugins/perfect-scrollbar/js/perfect-scrollbar.js"></c:url>"></script>
	<script
		src="<c:url value="/templates/assets/plugins/metismenu/js/metisMenu.min.js"></c:url>"></script>
	<script
		src="<c:url value="/templates/assets/plugins/simplebar/js/simplebar.min.js"></c:url>"></script>
	<script src="<c:url value="/templates/assets/js/pace.min.js"></c:url>"></script>
	<script src="<c:url value='/templates/assets/plugins/fancy-file-uploader/jquery.ui.widget.js' />"></script>
	<script src="<c:url value='/templates/assets/plugins/fancy-file-uploader/jquery.fileupload.js' />"></script>
	<script src="<c:url value='/templates/assets/plugins/fancy-file-uploader/jquery.iframe-transport.js' />"></script>
	<script src="<c:url value='/templates/assets/plugins/fancy-file-uploader/jquery.fancy-fileupload.js' />"></script>
	
	<script src="<c:url value='/templates/assets/plugins/datatable/js/jquery.dataTables.min.js' />"></script>
	<script src="<c:url value='/templates/assets/plugins/datatable/js/dataTables.bootstrap5.min.js' />"></script>
	<!--BS Scripts-->
	<script
		src="<c:url value="/templates/assets/js/bootstrap.bundle.min.js"></c:url>"></script>
	<script src="<c:url value="/templates/assets/js/main.js"></c:url>"></script>
	
	<script>
	    $('#fancy-file-upload').FancyFileUpload({
	        params: {
	            action: 'fileuploader'
	        },
	        maxfilesize: 1000000
	    });
	</script>
	<script>
	    $('#fancy-file-upload2').FancyFileUpload({
	        params: {
	            action: 'fileuploader'
	        },
	        maxfilesize: 1000000
	    });
	</script>
	<script>
		$(document).ready(function() {
			var table = $('#example2').DataTable( {
				lengthChange: false,
				buttons: [ 'copy', 'excel', 'pdf', 'print']
			} );
		 
			table.buttons().container()
				.appendTo( '#example2_wrapper .col-md-6:eq(0)' );
		} );
	 </script>
</body>
</html>