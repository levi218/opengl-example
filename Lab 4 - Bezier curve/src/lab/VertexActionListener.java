/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab;

import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 *
 * @author theph
 */
public interface VertexActionListener {

    void onVertexPositionChanged();

    void onDelete(GraphVertex v);
}
