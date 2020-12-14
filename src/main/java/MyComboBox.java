import javax.swing.*;
import java.util.List;

public class MyComboBox<T extends MyComboBox.GetId> {
    private final JComboBox<Item<T>> comboBox;
    private final Caster<T> caster;
    private List<T> list;

    public MyComboBox(Caster<T> caster) {
        comboBox = new JComboBox<>();
        this.caster = caster;
    }

    public MyComboBox() {
        comboBox = new JComboBox<>();
        caster = T::toString;
    }

    public void update(List<T> list) {
        this.list = list;
        comboBox.removeAllItems();
        list.forEach((v) -> comboBox.addItem(new Item<>(caster.cast(v), v)));
    }

    public T getSelectedItem() {
        return comboBox.getSelectedItem() != null ?
                ((Item<T>) comboBox.getSelectedItem()).getElement() : null;
    }

    public JComboBox<Item<T>> getComboBox() {
        return comboBox;
    }

    public interface Caster<T> {
        String cast(T element);
    }

    public interface GetId {
        int getId();
    }

    private static class Item<T> {
        private final String text;
        private final T element;

        public Item(String text, T element) {
            this.text = text;
            this.element = element;
        }

        @Override
        public String toString() {
            return text;
        }

        public T getElement() {
            return element;
        }



    }

}