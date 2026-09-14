document.addEventListener("DOMContentLoaded", function () {

    // =========================
    // DASHBOARD TABS
    // =========================

    const tabs = document.querySelectorAll(".nav-tab");
    const views = document.querySelectorAll(".tab-view");

    tabs.forEach(function (tab) {
        tab.addEventListener("click", function () {

            const targetId = tab.getAttribute("data-tab");

            tabs.forEach(function (item) {
                item.classList.remove("active");
            });

            views.forEach(function (view) {
                view.classList.remove("active-view");
            });

            tab.classList.add("active");

            const targetView = document.getElementById(targetId);

            if (targetView) {
                targetView.classList.add("active-view");
            }
        });
    });


    // =========================
    // NEW BOOKING MODAL
    // =========================

    const bookingModal =
        document.getElementById("bookingModal");

    const openBookingButton =
        document.getElementById("openBookingModal");

    const openBookingEmptyButton =
        document.getElementById("openBookingModalEmpty");

    const closeBookingButton =
        document.getElementById("closeBookingModal");

    const cancelBookingButton =
        document.getElementById("cancelBookingModal");

    const searchBookingButtons =
        document.querySelectorAll(".open-booking-from-search");


    function openBookingModal() {

        if (bookingModal) {
            bookingModal.classList.add("show");
            document.body.style.overflow = "hidden";
        }
    }


    function closeBookingModal() {

        if (bookingModal) {
            bookingModal.classList.remove("show");
            document.body.style.overflow = "";
        }
    }


    if (openBookingButton) {
        openBookingButton.addEventListener(
            "click",
            openBookingModal
        );
    }


    if (openBookingEmptyButton) {
        openBookingEmptyButton.addEventListener(
            "click",
            openBookingModal
        );
    }


    searchBookingButtons.forEach(function (button) {
        button.addEventListener(
            "click",
            openBookingModal
        );
    });


    if (closeBookingButton) {
        closeBookingButton.addEventListener(
            "click",
            closeBookingModal
        );
    }


    if (cancelBookingButton) {
        cancelBookingButton.addEventListener(
            "click",
            closeBookingModal
        );
    }


    if (bookingModal) {

        bookingModal.addEventListener(
            "click",
            function (event) {

                if (event.target === bookingModal) {
                    closeBookingModal();
                }
            }
        );
    }


    document.addEventListener(
        "keydown",
        function (event) {

            if (event.key === "Escape") {
                closeBookingModal();
            }
        }
    );


    // =========================
    // NOTIFICATION DROPDOWN
    // =========================

    const notificationButton =
        document.getElementById("notificationButton");

    const notificationDropdown =
        document.getElementById("notificationDropdown");


    if (notificationButton && notificationDropdown) {

        notificationButton.addEventListener(
            "click",
            function (event) {

                event.stopPropagation();

                notificationDropdown
                    .classList
                    .toggle("show");
            }
        );


        notificationDropdown.addEventListener(
            "click",
            function (event) {

                event.stopPropagation();
            }
        );


        document.addEventListener(
            "click",
            function () {

                notificationDropdown
                    .classList
                    .remove("show");
            }
        );
    }


    // =========================
    // BOOKING STATUS FILTER
    // =========================

    const bookingStatusFilter =
        document.getElementById("bookingStatusFilter");


    if (bookingStatusFilter) {

        bookingStatusFilter.addEventListener(
            "change",
            function () {

                const selectedStatus =
                    bookingStatusFilter.value;

                const bookingRows =
                    document.querySelectorAll(
                        ".booking-table tbody tr"
                    );


                bookingRows.forEach(function (row) {

                    const rowStatus =
                        row.getAttribute("data-status");

                    if (
                        selectedStatus === "ALL" ||
                        selectedStatus === rowStatus
                    ) {
                        row.style.display = "";
                    } else {
                        row.style.display = "none";
                    }
                });
            }
        );
    }


    // =========================
    // FACILITY SEARCH
    // =========================

    const facilitySearch =
        document.getElementById("facilitySearch");


    if (facilitySearch) {

        facilitySearch.addEventListener(
            "input",
            function () {

                const searchText =
                    facilitySearch
                        .value
                        .trim()
                        .toLowerCase();

                const facilityCards =
                    document.querySelectorAll(
                        ".facility-result"
                    );


                facilityCards.forEach(function (card) {

                    const cardText =
                        card.innerText.toLowerCase();

                    if (cardText.includes(searchText)) {
                        card.style.display = "";
                    } else {
                        card.style.display = "none";
                    }
                });
            }
        );
    }

});