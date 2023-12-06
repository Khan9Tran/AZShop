<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp"%> 

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" type="text/css" href="<c:url value="/templates/account/css/bootstrap.min.css" />">
    <link rel="stylesheet" type="text/css" href="<c:url value="/templates/account/css/fontawesome-all.min.css" />">
    <link rel="stylesheet" type="text/css" href="<c:url value="/templates/account/css/iofrm-style.css" />">
    <link rel="stylesheet" type="text/css" href="<c:url value="/templates/account/css/iofrm-theme22.css" />">
</head>
<body>
    <div class="form-body without-side">
        <div class="row">
            <div class="img-holder">
                <div class="bg"></div>
                <div class="info-holder">
                    <img src="<c:url value="/templates/account/img/graphic3.svg"></c:url>" alt="">
                </div>
            </div>
            <div class="form-holder">
                <div class="form-content">
                    <div class="form-items">
                        <h3>Verify Code</h3>
                        <br>
                        <form action="verify-customer" method="post">
                            <input class="form-control" type="text" name="verify-code" placeholder="Verify Code" required>
                            <div class="form-button">
                                <button id="submit" type="submit" class="ibtn">Submit</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="<c:url value='/templates/account/js/jquery.min.js' />"></script>
    <script src="<c:url value='/templates/account/js/popper.min.js' />"></script>
    <script src="<c:url value='/templates/account/js/bootstrap.min.js' />"></script>
    <script src="<c:url value='/templates/account/js/main.js' />"></script>
</body>
</html>