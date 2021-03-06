package comprafacil.myapp.root.comprafacil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import controller.ButtonAgregarOnClick;
import controller.ButtonEliminarOnClick;
import controller.ButtonRestarOnClick;
import controller.ControladorInventario;
import controller.EditTextCategoriaWatcher;
import controller.EditTextNombreWatcher;
import controller.EditTextPrecioWatcher;
import model.Producto;

public class GestionarInventario extends AppCompatActivity {

    private ArrayList<Producto> listaProductos;
    private AdaptadorProducto adaptador;
    private ListView listView1;
    private GestionarInventario vista = this;
    private Spinner spinner1;
    private String [] opciones = {"Bebidas", "Botanas", "Dulces", "Galletas", "Postres", "Sabritas"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hace la actividad FULLSCREEN
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gestionar_inventario);

        listaProductos = new ArrayList<>();
        //ProvisionalInventario provisionalInventario = new ProvisionalInventario();
        ControladorInventario controladorInventario = new ControladorInventario(this);
        listaProductos = controladorInventario.getInventario();

        adaptador = new AdaptadorProducto(this);
        listView1 = (ListView) findViewById(R.id.listView_ModificarInventario);
        listView1.setAdapter(adaptador);

        //Creando el adaptador del spinner de categorias
        spinner1 = (Spinner) findViewById(R.id.spinnerCategorias);
        ArrayAdapter adaptadorCategorias= new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner1.setAdapter(adaptadorCategorias);

    }

    public int agregarNuevoProducto(View view){
        try{
            EditText editTextNombre = (EditText)findViewById(R.id.editText_NombreProducto);
            //TextView textViewCategoria = (TextView) findViewById(R.id.tv_spinner_categoria);
            EditText editTextPrecio = (EditText)findViewById(R.id.editText_PrecioProducto);
            EditText editTextCantidad = (EditText)findViewById(R.id.editText_CantidadProducto);

            String nombre = editTextNombre.getText().toString();
            String categoria = spinner1.getSelectedItem().toString();
            float precio = Float.valueOf(editTextPrecio.getText().toString());
            int cantidad = Integer.valueOf(editTextCantidad.getText().toString());

            for(int i=0;i<listaProductos.size();i++){
                if(listaProductos.get(i).getNombre().compareTo(nombre) == 0 && listaProductos.get(i).getCategoria().compareTo(categoria) == 0 ){
                    int j = listaProductos.get(i).getCantidad();
                    listaProductos.get(i).setCantidad(j + cantidad);
                    adaptador.notifyDataSetChanged();
                    return 0;
                }
            }
            listaProductos.add(new Producto(categoria, nombre, precio, cantidad));
            ControladorInventario controladorInventario = new ControladorInventario(this);
            controladorInventario.agregarNuevoProducto(categoria,nombre,precio,cantidad);
            adaptador.notifyDataSetChanged();
            return 0;
        }catch (Exception e){
            Toast toast = Toast.makeText(this,"Rellene todos los campos",Toast.LENGTH_SHORT);
            toast.show();
        }
        return 0;

    }

    public void actualizarInventario(View view){
        ControladorInventario controladorInventario = new ControladorInventario(this);
        controladorInventario.actualizarInventario(listaProductos);
        adaptador.notifyDataSetChanged();
        Toast toast = Toast.makeText(this,"Datos actualizados", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void restablecerInventario(View view){
        ControladorInventario controladorInventario = new ControladorInventario(this);
        controladorInventario.restablecerInventario();
        listaProductos.clear();
        adaptador.notifyDataSetChanged();
    }

//clase del adaptador
   class AdaptadorProducto extends ArrayAdapter<Producto> {

        AppCompatActivity appCompatActivity;

        AdaptadorProducto(AppCompatActivity context) {
            super(context, R.layout.list_item_gestionar_inventario, listaProductos);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.list_item_gestionar_inventario, null);

            EditText categoria = (EditText) item.findViewById(R.id.editText_Categoria);
            categoria.setText(String.valueOf(listaProductos.get(position).getCategoria()));
            categoria.addTextChangedListener(new EditTextCategoriaWatcher(position, listaProductos, categoria, appCompatActivity));

            EditText nombre = (EditText) item.findViewById(R.id.editText_Producto);
            nombre.setText(String.valueOf(listaProductos.get(position).getNombre()));
            nombre.addTextChangedListener(new EditTextNombreWatcher(position, listaProductos, nombre, appCompatActivity));

            EditText precio = (EditText) item.findViewById(R.id.editText_Precio);
            precio.setText(String.valueOf(listaProductos.get(position).getPrecio()));
            precio.addTextChangedListener(new EditTextPrecioWatcher(position, listaProductos, precio, appCompatActivity));

            EditText cantidad = (EditText) item.findViewById(R.id.editText_Cantidad);
            cantidad.setHint(String.valueOf(listaProductos.get(position).getCantidad()));

            Button buttonAgregarProducto = (Button)item.findViewById(R.id.button_agregarProducto);
            buttonAgregarProducto.setOnClickListener(new ButtonAgregarOnClick(position,cantidad,listaProductos,appCompatActivity));

            Button buttonRestarProducto = (Button)item.findViewById(R.id.button_restarProducto);
            buttonRestarProducto.setOnClickListener(new ButtonRestarOnClick(position,cantidad,listaProductos,appCompatActivity));

            Button buttonEliminarProducto = (Button)item.findViewById(R.id.button_eliminarProducto);
            buttonEliminarProducto.setOnClickListener(new ButtonEliminarOnClick(position,listaProductos,appCompatActivity,adaptador,vista));
          return (item);
        }
    }

}
