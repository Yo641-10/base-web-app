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
        var html_tpl = '<div class="post-preview">' +
            '<a href="../../../../../startbootstrap-clean-blog-gh-pages/post.html">' +
            '<h2 class="post-title">Man must explore, and this is exploration at its greatest</h2>' +
            '<h3 class="post-subtitle">Problems look mighty small from 150 miles up</h3>' +
            '</a>' +
            '<p class="post-meta">作者：' +
            '<a href="#">Start Bootstrap</a>' +
            '</p></div><hr>';
    });
})(jQuery); // End of use strict
