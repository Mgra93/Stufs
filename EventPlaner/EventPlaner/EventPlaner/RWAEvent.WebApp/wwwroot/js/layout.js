document.addEventListener("DOMContentLoaded", function () {
    const btnShowCart = document.getElementById("btnShowCart");
    if (btnShowCart) {
        btnShowCart.addEventListener("click", function (e) {
            e.preventDefault();
            fetch("/Reservation/GetCartPartial")
                .then(res => res.text())
                .then(html => {
                    document.getElementById("cartModalBody").innerHTML = html;
                    updateCartCount();
                    new bootstrap.Modal(document.getElementById("cartModal")).show();
                });
        });
    }

    document.body.addEventListener("click", async function (e) {
        const btn = e.target.closest(".btn-remove-item");
        if (!btn) return;

        const eventId = btn.getAttribute("data-id");
        const response = await fetch(`/Reservation/RemoveFromCart?eventId=${eventId}`);
        const result = await response.json();

        if (result.success) {
            const row = document.getElementById(`cart-item-${eventId}`);
            if (row) {
                const lineTotal = parseFloat(row.querySelector("strong")?.textContent.replace(" €", "")) || 0;
                row.remove();

                const totalEl = document.getElementById("cart-total");
                if (totalEl) {
                    totalEl.textContent = (parseFloat(totalEl.textContent) - lineTotal).toFixed(2);
                }
            }

            if (result.empty) {
                document.getElementById("cart-container").innerHTML = `
                            <div class="text-center p-3">
                                <i class="bi bi-cart-x" style="font-size: 2rem;"></i>
                                <p class="mt-2">Košarica je prazna</p>
                            </div>`;
            }

            updateCartCount();
        }
    });

    async function updateCartCount() {
        try {
            const res = await fetch("/Reservation/GetCartCount");
            const data = await res.json();
            const cartCountEl = document.getElementById("cartCount");
            if (cartCountEl) cartCountEl.textContent = data.eventNumber || 0;
        } catch (err) {
            console.error("Neuspjelo dohvaćanje broja stavki:", err);
        }
    }

    updateCartCount();
});