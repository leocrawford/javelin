package com.crypticbit.diff.demo.swing;

import javax.swing.JFrame;

/**
 * Interface to allow a frame to behave like a dialog and communicate completion events to an owner.
 *
 * @author Stephen Owens
 *         <p>
 *         Copyright 2011 Stephen P. Owens : steve@doitnext.com
 *         </p>
 *         <p>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 *         compliance with the License. You may obtain a copy of the License at
 *         </p>
 *         <p>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         </p>
 *         <p>
 *         Unless required by applicable law or agreed to in writing, software distributed under the License is
 *         distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 *         the License for the specific language governing permissions and limitations under the License.
 *         </p>
 */
public interface OKCancelListener {
    public void onFrameAction(Action action, JFrame frame);;

    public enum Action {
	OK, CANCEL, DEFAULT_CLOSE
    }
}
