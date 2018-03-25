(function ($) {
    "use strict"; // Start of use strict

    // Floating label headings for the contact form
    $("body").on("input propertychange", ".floating-label-form-group", function (e) {
        $(this).toggleClass("floating-label-form-group-with-value", !!$(e.target).val());
    }).on("focus", ".floating-label-form-group", function () {
        $(this).addClass("floating-label-form-group-with-focus");
    }).on("blur", ".floating-label-form-group", function () {
        $(this).removeClass("floating-label-form-group-with-focus");
    });

    // Show the navbar when the page is scrolled up
    var MQL = 992;

    //primary navigation slide-in effect
    if ($(window).width() > MQL) {
        var headerHeight = $('#mainNav').height();
        $(window).on('scroll', {
                previousTop: 0
            },
            function () {
                var currentTop = $(window).scrollTop();
                //check if user is scrolling up
                if (currentTop < this.previousTop) {
                    //if scrolling up...
                    if (currentTop > 0 && $('#mainNav').hasClass('is-fixed')) {
                        $('#mainNav').addClass('is-visible');
                    } else {
                        $('#mainNav').removeClass('is-visible is-fixed');
                    }
                } else if (currentTop > this.previousTop) {
                    //if scrolling down...
                    $('#mainNav').removeClass('is-visible');
                    if (currentTop > headerHeight && !$('#mainNav').hasClass('is-fixed')) $('#mainNav').addClass('is-fixed');
                }
                this.previousTop = currentTop;
            });
    }
    $.get("/action/list-novels", function (data) {
        console.log(data);
        var jsondata = JSON.parse(data);
        if (Number(jsondata["status"]) == 400) {
            var novels_list = jsondata["data"];
            for (var i = 0; i < novels_list.length; ++i) {
                var item = novels_list[i];
                var html_tpl = '<div class="post-preview">' +
                    '<a href="' + item["url"] + '">' +
                    '<h2 class="post-title">' + item["title"] + '</h2>' +
                    '<h3 class="post-subtitle">' + item["subtitle"] + '</h3>' +
                    '</a>' +
                    '<p class="post-meta">作者：' +
                    '<a href="#">' + item["autho"] + '</a>' + item["date"]++
                '</p></div><hr>';
                $(".main-novels").append(html_tpl);
            }

        }

    });
})(jQuery); // End of use strict
