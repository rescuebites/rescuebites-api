package com.rescuebites.api.cart.data.mappers;

import com.rescuebites.api.cart.controllers.responses.CartItemResponse;
import com.rescuebites.api.cart.controllers.responses.CartResponse;
import com.rescuebites.api.cart.controllers.responses.CommerceCartSummary;
import com.rescuebites.api.cart.data.models.Cart;
import com.rescuebites.api.cart.data.models.CartItem;
import com.rescuebites.api.client.data.mappers.ImageMapper;
import com.rescuebites.api.order.data.enums.PaymentMethod;
import com.rescuebites.api.product.data.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.rescuebites.api.cart.utils.CartConstants.SERVICE_FEE;

public class CartMapper {

    public static CartItemResponse toCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal unitPrice = product.getDiscountedPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return new CartItemResponse(
                cartItem.getCartItemId(),
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getOriginalPrice(),
                product.getDiscountPercentage(),
                unitPrice,
                cartItem.getQuantity(),
                product.getStock(),
                subtotal,
                product.getCommerce().getCommerceId(),
                product.getCommerce().getName(),
                product.getCommerce().getAddress(),
                product.getImages().stream()
                        .map(ImageMapper::toImageResponse)
                        .collect(Collectors.toList())
        );
    }

    public static CartResponse toCartResponse(Cart cart, PaymentMethod selectedPaymentMethod, PaymentMethod lastUsedPaymentMethod) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartMapper::toCartItemResponse)
                .toList();

        // Agrupar por comercio
        Map<UUID, CommerceCartSummary> commerceSummaries = items.stream()
                .collect(Collectors.groupingBy(
                        CartItemResponse::commerceId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                itemsList -> {
                                    if (itemsList.isEmpty()) return null;
                                    CartItemResponse first = itemsList.get(0);
                                    BigDecimal commerceSubtotal = itemsList.stream()
                                            .map(CartItemResponse::subtotal)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                                    return new CommerceCartSummary(
                                            first.commerceId(),
                                            first.commerceName(),
                                            first.commerceAddress(),
                                            itemsList,
                                            commerceSubtotal,
                                            itemsList.size()
                                    );
                                }
                        )
                ));

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal serviceFee = SERVICE_FEE;
        BigDecimal total = subtotal.add(serviceFee);

        // Usar el método de pago seleccionado, o el último si no hay seleccionado
        PaymentMethod paymentMethodToUse = selectedPaymentMethod != null ? selectedPaymentMethod : lastUsedPaymentMethod;

        return new CartResponse(
                cart.getCartId(),
                commerceSummaries,
                subtotal,
                serviceFee,
                total,
                items.size(),
                paymentMethodToUse,
                lastUsedPaymentMethod
        );
    }
}