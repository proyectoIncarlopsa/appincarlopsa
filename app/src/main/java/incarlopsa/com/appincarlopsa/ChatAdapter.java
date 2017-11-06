package incarlopsa.com.appincarlopsa;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<DataBaseItem> listaCabecera;

    public ChatAdapter(ArrayList<DataBaseItem> listaCabecera) {
        this.listaCabecera = listaCabecera;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cabecera_chat, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                if(mListener!=null) {
                    mListener.onItemClick(listaCabecera.get(position), position);
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat cabecera = (Chat)listaCabecera.get(position);

        //Recuperar el usuario que ha creado el chat
        ArrayList<DataBaseItem> resultados = new ArrayList<>();
        Usuario usuarioAux = new Usuario();
        Integer idAutor = cabecera.getIdUsuario();
        usuarioAux.setIdUsuario(idAutor);
        HiloParaRead hiloParaRead = new HiloParaRead(new DAOUsuario());
        try {
            resultados = hiloParaRead.execute(usuarioAux).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resultados.size() > 0){ //deberia serlo
            usuarioAux = (Usuario)resultados.get(0);
        }else{ //Por si acaso
            usuarioAux.setNombre("Desconocido");
            usuarioAux.setApellidos("Desconocido");
        }

        String titulo = cabecera.getTitulo();
        String autor = usuarioAux.getNombre() + " "
                    + usuarioAux.getApellidos();

        String fechaCreacion = cabecera.getFechaCreacion();
        String horaCreacion = cabecera.getHoraCreacion();
        String fechaUpdate = cabecera.getFechaUltimoUpdate();
        String horaUpdate = cabecera.getHoraUltimoUpdate();

        holder.titulo.setText(titulo);
        holder.idAutor.setText(idAutor.toString());
        holder.autor.setText(autor);
        holder.fechaCreacion.setText(fechaCreacion);
        holder.horaCreacion.setText(horaCreacion);
        holder.fechaUpdate.setText(fechaUpdate);
        holder.horaUpdate.setText(horaUpdate);

    }

    @Override
    public int getItemCount() {
        return listaCabecera.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo;
        private TextView idAutor;
        private TextView autor;
        private TextView fechaCreacion;
        private TextView horaCreacion;
        private TextView fechaUpdate;
        private TextView horaUpdate;

        public ViewHolder(View v) {
            super(v);
            titulo = (TextView) v.findViewById(R.id.textUserName);
            idAutor = (TextView) v.findViewById(R.id.textUserName);
            autor = (TextView) v.findViewById(R.id.textUserName);
            fechaCreacion = (TextView) v.findViewById(R.id.textUserName);
            horaCreacion = (TextView) v.findViewById(R.id.textUserName);
            fechaUpdate = (TextView) v.findViewById(R.id.textUserName);
            horaUpdate = (TextView) v.findViewById(R.id.textUserName);

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(DataBaseItem item, int position);
    }
    private ChatAdapter.OnItemClickListener mListener;

    public void setOnItemListener(ChatAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

}