package com.janice.osc.Customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.janice.osc.Model.Order;
import com.janice.osc.Payment.PaymentsUtil;
import com.janice.osc.Util.ListAdapter;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.janice.osc.Model.Producto;
import com.janice.osc.R;
import com.janice.osc.Util.GridAdapter;
import com.janice.osc.Util.Util;
import com.janice.osc.Util.Values;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import in.srain.cube.views.GridViewWithHeaderAndFooter;


public class SodaProductsFragment extends Fragment {

    private View v;
    private TextView mNombreSoda;
    private Button mOrdenarButton;
    private List<Producto> mProductos;
    private GridViewWithHeaderAndFooter mGrid;
    private String sodaId;
    private int total_prod = 0; //Total de productos pedidos por el cliente
    private int monto_total = 0;
    private List<Producto> orden;
    private FirebaseFirestore db;
    private AlertDialog recibo;

    private PaymentsClient mPaymentsClient;
    private View mGooglePayButton;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private TextView mGooglePayStatusText;

    public SodaProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soda_products, container, false);
        v = view;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Fijar verticalmente
        setItems(view);
        setListeners();
        cargarProductos();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mProductos == null)
            cargarProductos();
    }

    private void setItems(View view) {
        db = FirebaseFirestore.getInstance();
        mGrid = view.findViewById(R.id.gridview); //Obtención del grid view
        mNombreSoda = view.findViewById(R.id.nombre_soda);
        mOrdenarButton = view.findViewById(R.id.ordenar_button);
        mNombreSoda.setText(Util.nameSodaSelected);
        sodaId = Util.idSodaSelected;
        mProductos = new ArrayList<>();
        orden = new ArrayList<>();



    }

    private void setListeners() {
        mOrdenarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ordenar();
            }
        });
    }

    private void cargarProductos() {
        mProductos = new ArrayList<>(); //Resetear lista de productos para volverla a cargar desde 0
        db.collection("usuarios").document(sodaId)//De la soda actual...
                .collection("productos") //Traigame los productos...
                .whereEqualTo("estado_cantidad", Values.ACTIVO)
                .get() //Vamos al get de una vez (sin el where) porque quiero todos los productos de la soda
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mProductos.add(document.toObject(Producto.class));
                            }
                            //Mostrar los objetos en el Grid
                            setUpGridView(mGrid); //Inicializar el grid view
                        }
                    }
                });
    }

    /**
     * Infla el grid view del fragmento dependiendo de la sección
     *
     * @param grid Instancia del grid view
     */
    private void setUpGridView(GridViewWithHeaderAndFooter grid) {
        if (mProductos.size() > 0) {
            grid.addHeaderView(createHeaderView(mProductos.get(0))); //El plato principal siempre estara en la primera posicion
            List<Producto> productos_sin_plato_principal = new ArrayList<>(mProductos); //Siempre hay que enviar la lista sin el plato principal al Adapter
            productos_sin_plato_principal.remove(0);
            grid.setAdapter(new GridAdapter<Producto>(getActivity(), productos_sin_plato_principal, SodaProductsFragment.this, R.layout.template_ingrediente_customer));
        }
    }

    /**
     * Crea un view de cabecera para mostrarlo en el principio del grid view.
     *
     * @return Header View
     */
    private View createHeaderView(final Producto item) {
        View view;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.template_plato_del_dia, null, false);

        //Seteando Imagen
        ImageView image = (ImageView) view.findViewById(R.id.imagen);
        if (!item.getImg().equals("")) //Solo seteamos una imagen si el objeto trae una. Si no trae ninguna, se queda con la imagen default del layout
            Glide.with(image.getContext()).load(item.getImg()).into(image);

        // Seteando Titulo
        TextView name = (TextView) view.findViewById(R.id.titulo);
        name.setText(item.getTitulo());

        // Seteando Descripción
        TextView descripcion = (TextView) view.findViewById(R.id.descripcion);
        descripcion.setText(item.getDescripcion());

        // Seteando Precio
        TextView precio = (TextView) view.findViewById(R.id.precio);
        precio.setText(String.format("%s %s", getString(R.string.simbolo_colones), item.getPrecio().toString()));

        // Mostrar NumberPicker
        final NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        numberPicker.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        numberPicker.requestLayout();//Esta linea es para refrescar la pantalla
        numberPicker.setValue(0); //Por default, siempre comienza en 0...
        numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                //int incremento = (action == ActionEnum.INCREMENT) ? 1 : -1;
                //agregarAlPedido(item, numberPicker.getValue(), incremento);
                if(action == ActionEnum.INCREMENT){
                    agregarAlPedido(item, numberPicker.getValue());
                }
                else{
                    removerDelPedido(item, numberPicker.getValue());
                }
            }
        });

        return view;
    }

    public void agregarAlPedido(Producto producto_escogido, int cantidad/*, int incremento*/) {
        /*total_prod += incremento; //Aqui incrementa o decrementa...
        mOrdenarButton.setEnabled(total_prod > 0); //Habilitamos el boton de ordenar porque el cliente ya ordeno algo
        orden.add(new Producto(producto_escogido, cantidad));*/

        boolean es_nuevo = true;
        total_prod++;
        mOrdenarButton.setEnabled(total_prod > 0); //Habilitamos el boton de ordenar cuando el cliente ya ordeno algo
        monto_total += producto_escogido.getPrecio();

        for(Producto p : orden) { //Recorremos la lista de ordenes para ver si este producto ya estaba...
            if(p.getId().equals(producto_escogido.getId())){
                p.setEstado_cantidad(cantidad);
                es_nuevo = false;
            }
        }

        if(es_nuevo)
            orden.add(new Producto(producto_escogido, cantidad));
    }

    public void removerDelPedido(Producto producto_para_remover, int cantidad) {
        total_prod--; //Decrementa...
        mOrdenarButton.setEnabled(total_prod > 0); //Habilitamos el boton de ordenar cuando el cliente ya ordeno algo
        monto_total -= producto_para_remover.getPrecio();
        /*Iterator<Producto> i = orden.iterator();
        while (i.hasNext()) {
            Producto siguiente = i.next(); // must be called before you can call i.remove()
            if(i.getId().equals(producto_para_remover.getId())){
                if(cantidad>0){
                    i.setEstado_cantidad(cantidad);
                }
                else{
                    i.remove(p);
                }
            }
        }*/


        for(Producto p : orden) {
            if(p.getId().equals(producto_para_remover.getId())){
                if(cantidad>0){
                    p.setEstado_cantidad(cantidad);
                }
                else{
                    orden.remove(p); //TODO: Si se cae, cambiar al iterador :v
                }
            }
        }
    }

    private void ordenar() {
        //Mostramos alert dialog con el recibo
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewAux = inflater.inflate(R.layout.alert_dialog_recibo, null);
        builder.setView(viewAux);

        //Atributos de la vista del AlertDialog.Builder
        ListView lista_productos_pedido = viewAux.findViewById(R.id.lista_productos_pedido);
        TextView total = viewAux.findViewById(R.id.total);
        Button confirm_button = viewAux.findViewById(R.id.confirm_button);
        Button cancel_button = viewAux.findViewById(R.id.cancel_button);


        // Initialize a Google Pay API client for an environment suitable for testing.
        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        mPaymentsClient = PaymentsUtil.createPaymentsClient(getActivity());
        mGooglePayButton = viewAux.findViewById(R.id.googlepay_button);
        mGooglePayStatusText = viewAux.findViewById(R.id.googlepay_status);
        mGooglePayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPayment(view);
                    }
                });
        possiblyShowGooglePayButton();

        total.setText(String.format("%s %d", getString(R.string.simbolo_colones), monto_total));
        setUpListViewDelPedido(lista_productos_pedido);

        recibo = builder.create();

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                confirmarOrden();
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                recibo.dismiss();
            }
        });

        recibo.show();
    }

    /**
     * Infla el list view del fragmento dependiendo de la sección
     *
     * @param list Instancia de  la lista
     */

    private void setUpListViewDelPedido(ListView list) {
        if(orden.size()>0){
            list.setAdapter(new ListAdapter<Producto>(getActivity(), orden, SodaProductsFragment.this, R.layout.template_producto_pedido));
        }
    }

    private void confirmarOrden() {
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Order nueva_orden = new Order("",sodaId, customerId, Values.PENDIENTE ,orden, monto_total);
        db.collection("ordenes").add(nueva_orden) //Guardar en la BD
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        actualizarIdNuevaOrden(documentReference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al ordenar", Toast.LENGTH_LONG).show();
                    }
                });
        recibo.dismiss(); //Cerrar recibo
    }

    private void actualizarIdNuevaOrden(DocumentReference documentReference){
        db.collection("ordenes").document(documentReference.getId())
                .update("id", documentReference.getId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Orden realizada con éxito", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error al ordenar", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ===> Google Pay

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see <a href=
     *     "https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html#isReadyToPay(com.google.android.gms.wallet.IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(getActivity(),
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }

    /**
     * If isReadyToPay returned {@code true}, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns {@code
     * false}.
     *
     * @param available isReadyToPay API response.
     */
    private void setGooglePayAvailable(boolean available) {
        if (available) {
            try {
                mGooglePayStatusText.setVisibility(View.GONE);
                mGooglePayButton.setVisibility(View.VISIBLE);
            }
            catch(Exception e){
                System.out.println(e);

            }
        } else {
            mGooglePayStatusText.setText(R.string.googlepay_status_unavailable);
        }
    }


    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     *
     * @param requestCode Request code originally supplied to AutoResolveHelper in requestPayment().
     * @param resultCode Result code returned by the Google Pay API.
     * @param data Intent from the Google Pay API containing payment or error data.
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     *     from an Activity</a>
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

                // Re-enables the Google Pay payment button.
                mGooglePayButton.setClickable(true);
                break;
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a
     *     href="https://developers.google.com/pay/api/android/reference/object#PaymentData">Payment
     *     Data</a>
     */
    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d("BillingName", billingName);
            //Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     *     WalletConstants.ERROR_CODE_* constants.
     * @see <a
     *     href="https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants#constant-summary">
     *     Wallet Constants Library</a>
     */
    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    // This method is called when the Pay with Google button is clicked.
    public void requestPayment(View view) {
        // Disables the button to prevent multiple clicks.
        mGooglePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = PaymentsUtil.microsToString(monto_total);

        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), getActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

}
