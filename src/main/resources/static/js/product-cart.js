function changeQuantity(button, delta) {
    const input = button.parentElement.querySelector('input[type="number"]');
    let val = parseInt(input.value || "1");
    val += delta;
    if (val < 1) val = 1;
    input.value = val;
}
