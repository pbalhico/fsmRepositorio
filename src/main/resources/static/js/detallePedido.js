document.addEventListener('DOMContentLoaded', function () {

    const addButtons = document.querySelectorAll('.add-item-btn');

    const concluirButton = document.getElementById('concluirPedidoBtn');

    const pedidoId = document.getElementById('pedidoIdInput').value;

    const returnUrlInput = document.getElementById('returnUrlInput');

    const returnUrl = returnUrlInput ? returnUrlInput.value : '/pedidos/lista';


    const pedidoEstadoElement = document.getElementById('pedidoEstado');

// Obtener el estado actual del pedido de forma robusta

    const currentEstado = pedidoEstadoElement ? pedidoEstadoElement.dataset.estado || pedidoEstadoElement.textContent.trim() : '';


// Set to track "added" item IDs (purely for UI)

    const addedItems = new Set();


// Determine if the order is already in a final state

    const isCompletedOrArchived = currentEstado === 'Completado' || currentEstado === 'Archivado';


// Function to handle server responses (success or error)
    function handleResponse(response) {
        if (!response.ok) {

            return response.json().then(err => {

                const errorMessage = err.errorMessage || err.error || 'Error desconocido del servidor.';

                throw new Error(errorMessage);

            }).catch(() => {

                throw new Error(`Error en la petición: ${response.status} ${response.statusText}`);

            });

        }

        return response.json();

    }


// Logic for "Add" buttons

    addButtons.forEach(button => {

        const itemId = button.dataset.itemId;

        const row = button.closest('tr');

        const checkbox = row.querySelector('.item-added-checkbox');


// If the order is completed or archived, disable the "Add" button and change its appearance

        if (isCompletedOrArchived) {

            button.disabled = true;

            button.textContent = 'Pedido Finalizado';

            button.classList.remove('btn-success');

            button.classList.add('btn-secondary');

            if (checkbox) {

                checkbox.checked = true; // Assume all items are "added" if order is finalized

            }

        } else {

// Only add the click listener if the order is not finalized

            button.addEventListener('click', function () {

                if (!addedItems.has(itemId)) {

                    addedItems.add(itemId);


                    if (checkbox) {

                        checkbox.checked = true;

                    }


                    this.textContent = 'Añadido';

                    this.classList.remove('btn-success');

                    this.classList.add('btn-secondary');

                    this.disabled = true;


// If all items are "added" (visually), enable the "Concluir Pedido" button

                    if (addedItems.size === addButtons.length) {

                        concluirButton.disabled = false;

                    }

                }

            });

        }

    });


// Logic for the "Concluir Pedido" button

    if (concluirButton) {

// Initial state of the button (disabled by Thymeleaf, but reinforced here)

        const articulosTableBody = document.querySelector('.order-table tbody');

        const hasArticulos = articulosTableBody && articulosTableBody.querySelectorAll('tr:not(.empty-message)').length > 0;


// The button is disabled if the order is completed/archived OR if there are no articles

        const isDisabledInitial = isCompletedOrArchived || !hasArticulos;

        concluirButton.disabled = isDisabledInitial;


// If the order is already in a final state, update button text and style

        if (isCompletedOrArchived) {

            concluirButton.textContent = 'Pedido Finalizado';

            concluirButton.classList.remove('btn-primary');

            concluirButton.classList.add('btn-info'); // Or 'btn-secondary'

        } else {

// Add click event listener only if the order is NOT already finalized

            concluirButton.addEventListener('click', function () {

                if (confirm('¿Estás seguro de que quieres concluir este pedido? Esta acción es irreversible y establecerá la fecha de recepción.')) {

                    fetch(`/pedidos/concluir/${pedidoId}`, {

                        method: 'POST',

                        headers: {

                            'Content-Type': 'application/json',

// If using Spring Security CSRF, uncomment:

// 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content,

// 'X-CSRF-HEADER': document.querySelector('meta[name="_csrf_header"]').content

                        }

                    })

                        .then(handleResponse)

                        .then(data => {

                            alert(data.message || 'Pedido concluido exitosamente. Fecha de recepción actualizada.');

                            window.location.href = returnUrl; // Redirect to the list page

                        })

                        .catch(error => {

                            console.error('Error al concluir el pedido:', error);

                            alert('Fallo al concluir el pedido: ' + error.message);

                        });

                }

            });

        }

    }

});