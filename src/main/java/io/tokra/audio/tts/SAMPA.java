package io.tokra.audio.tts;

import java.util.Vector;

public class SAMPA {

	public static class SK {

		public static Vector<String> convertTextToSAMPA(char[] text) {
			Vector<String> sampa = new Vector<String>();
			int i = 0;
			boolean zapisal = false;
			while (i < text.length) {
				switch (text[i]) {
				case 'a':
					sampa.addElement("a");
					break;
				case 'ä':
					sampa.addElement("{");
					break;
				case 'e':
					sampa.addElement("E");
					break;
				case 'i':
					if (i < (text.length - 1)) {
						switch (text[i + 1]) {
						case 'a':
							sampa.addElement("I_^A");
							i++;
							zapisal = true;
							break; // !!!tu je vynimka - a, e, u maju opacnu
									// velkost
						case 'e':
							sampa.addElement("I_^e");
							i++;
							zapisal = true;
							break; // pretoze v indexovacom subore potom boli
									// dvojhlaska
						case 'u':
							sampa.addElement("I_^u");
							i++;
							zapisal = true;
							break; // ia a ja zapisane rovnako, problem pri
									// parsovani
						}
					}
					if (zapisal == false) {
						sampa.addElement("I");
					}
					break;
				case 'o':
					sampa.addElement("O");
					break;
				case 'u':
					sampa.addElement("U");
					break;
				case 'y':
					sampa.addElement("I");
					break;
				case 'á':
					sampa.addElement("a~");
					break;
				case 'é':
					sampa.addElement("E~");
					break;
				case 'í':
					sampa.addElement("I~");
					break;
				case 'ó':
					sampa.addElement("O~");
					break;
				case 'ô':
					sampa.addElement("U_^O");
					break;
				case 'ú':
					sampa.addElement("U~");
					break;
				case 'ý':
					sampa.addElement("I~");
					break;
				case 'm':
					sampa.addElement("m");
					break;
				case 'n':
					sampa.addElement("n");
					break;
				case 'ň':
					sampa.addElement("(J)");
					break;
				case 'l':
					sampa.addElement("l");
					break;
				case 'ľ':
					sampa.addElement("(L)");
					break;
				case 'ĺ':
					sampa.addElement("l=~");
					break;
				case 'r':
					sampa.addElement("r");
					break;
				case 'ŕ':
					sampa.addElement("r=~");
					break;
				case 'j':
					sampa.addElement("I_^");
					break;
				case 'b':
					sampa.addElement("b");
					break;
				case 'd':
					if (i < (text.length - 1)) {
						switch (text[i + 1]) {
						case 'z':
							sampa.addElement("dz");
							i++;
							zapisal = true;
							break;
						case 'ž':
							sampa.addElement("d(Z)");
							i++;
							zapisal = true;
							break;
						}
					}
					if (zapisal == false) {
						sampa.addElement("d");
					}
					break;
				case 'ď':
					sampa.addElement("J-");
					break;
				case 'g':
					sampa.addElement("g");
					break;
				case 'h':
					sampa.addElement("h-");
					break;
				case 'z':
					sampa.addElement("z");
					break;
				case 'ž':
					sampa.addElement("(Z)");
					break;
				case 'v':
					sampa.addElement("v");
					break;
				case 'p':
					sampa.addElement("p");
					break;
				case 't':
					sampa.addElement("t");
					break;
				case 'ť':
					sampa.addElement("c");
					break;
				case 'c':
					if (i < (text.length - 1)) {
						if (text[i + 1] == 'h') {
							sampa.addElement("x");
							i++;
							zapisal = true;
							break;
						}
					}
					if (zapisal == false) {
						sampa.addElement("ts");
					}
					break;
				case 'č':
					sampa.addElement("t(S)");
					break;
				case 'k':
					sampa.addElement("k");
					break;
				case 's':
					sampa.addElement("s");
					break;
				case 'š':
					sampa.addElement("(S)");
					break;
				case 'f':
					sampa.addElement("f");
					break;
				case ' ':
					sampa.addElement("_");
					sampa.addElement("_");
					break;
				}
				i++;
				zapisal = false;
			}
			return sampa;
		}

		public static Vector<String> mutation(Vector<String> sampa) { // prepis
																		// koli
																		// spodovaniu
			int i = 0;
			boolean set = false; // pomocna premnna koli pravidlu pre pismeno t
			boolean s = false; // ak je s true, znamena to ze s sa spodobilo na
								// z a pravidla pre z sa uz nekontroluju
			boolean d = false; // ak je d true, znamena to ze d sa spodobilo na
								// t a pravidla pre z sa uz nekontroluju

			while (i < sampa.size()) {

				/* mutation b */
				if (sampa.elementAt(i).equals("b")) {
					if (sampa.elementAt(i + 1).equals("ts")) {
						sampa.setElementAt("p", i);
					}
					if (sampa.elementAt(i + 1).equals("t(S)")) {
						sampa.setElementAt("p", i);
					}
					if (sampa.elementAt(i + 1).equals("s")) {
						sampa.setElementAt("p", i);
					}
					if (sampa.elementAt(i + 1).equals("(S)")) {
						sampa.setElementAt("p", i);
					}
					if (sampa.elementAt(i + 1).equals("k")) {
						sampa.setElementAt("p", i);
					}
				}

				/* mutation c */
				if (sampa.elementAt(i).equals("ts")) {
					if (sampa.elementAt(i + 1).equals("_")) {
						if (sampa.elementAt(i - 1).equals("E")) {
							sampa.setElementAt("dz", i);
						}
						if (sampa.elementAt(i - 1).equals("U")) {
							sampa.setElementAt("p", i);
						}
					}
					if (sampa.elementAt(i + 1).equals("m")) {
						sampa.setElementAt("dz", i);
					}
					if (sampa.elementAt(i - 1).equals("U_^O")) {
						sampa.setElementAt("dz", i);
					}
				}

				/* mutation i */
				if (i - 4 >= 0) { // kontrola aby som nevyskocila z pola
					if (sampa.elementAt(i).equals("J-")) {
						if (sampa.elementAt(i - 4).equals("_")) {
							sampa.setElementAt("ts", i);
						} else if (sampa.elementAt(i - 4).equals("E")) {
							sampa.setElementAt("ts", i);
						} else if (sampa.elementAt(i - 4).equals("I")) {
							sampa.setElementAt("ts", i);
						}
					}
				}

				/* mutation po d a vynimky, kedy sa de, di necita makko */
				if (i + 2 < sampa.size()) { // kontrola aby som nevyskocila z
											// pola
					if (sampa.elementAt(i).equals("d") && sampa.elementAt(i + 1).equals("E")) {
						if (sampa.elementAt(i + 2).equals("a~")) {
							sampa.setElementAt("d", i);
						} else if (sampa.elementAt(i + 2).equals("k")) {
							sampa.setElementAt("d", i);
						} else if (sampa.elementAt(i + 2).equals("(S)")) {
							sampa.setElementAt("d", i);
						} else if (sampa.elementAt(i + 2).equals("ts")) {
							sampa.setElementAt("d", i);
						} else {
							sampa.setElementAt("J-", i);
						}
					}

					if (sampa.elementAt(i).equals("d") && sampa.elementAt(i + 1).equals("I")) {
						if (sampa.elementAt(i + 2).equals("a~")) {
							sampa.setElementAt("d", i);
						} else if (sampa.elementAt(i + 2).equals("v")) {
							sampa.setElementAt("d", i);
						} else {
							sampa.setElementAt("J-", i);
						}
					}

					if (sampa.elementAt(i).equals("d") && sampa.elementAt(i + 1).equals("I~")) {
						if (sampa.elementAt(i + 2).equals("ts")) {
							sampa.setElementAt("d", i);
						} else if (sampa.elementAt(i + 2).equals("s")) {
							sampa.setElementAt("d", i);
						} else {
							sampa.setElementAt("J-", i);
						}
					}

					if (sampa.elementAt(i).equals("d")) {
						if (sampa.elementAt(i + 1).equals("_")) {
							if (sampa.elementAt(i + 2).equals("s")) {
								sampa.setElementAt("t", i);
							} else if (sampa.elementAt(i + 2).equals("p")) {
								sampa.setElementAt("t", i);
								d = true;
							} else if (sampa.elementAt(i + 2).equals("t")) {
								sampa.setElementAt("t", i);
								d = true;
							} else if (sampa.elementAt(i + 2).equals("ts")) {
								sampa.setElementAt("t", i);
								d = true;
							} else if (sampa.elementAt(i + 2).equals("t(S)")) {
								sampa.setElementAt("t", i);
								d = true;
							}
						}
					}
				}

				if (sampa.elementAt(i).equals("d")) {
					if (sampa.elementAt(i + 1).equals("x")) {
						sampa.setElementAt("t", i);
						d = true;
					} else if (sampa.elementAt(i + 1).equals("p")) {
						sampa.setElementAt("t", i);
						d = true;
					} else if (sampa.elementAt(i + 1).equals("(S)")) {
						sampa.setElementAt("t(S)", i);
					} else if (sampa.elementAt(i + 1).equals("k")) {
						sampa.setElementAt("t", i);
						d = true;
					} else if (sampa.elementAt(i + 1).equals("s")) {
						sampa.setElementAt("ts", i);
					} else {
						sampa.setElementAt("d", i);
					}
				}

				if (i == (sampa.size() - 2)) { // kontrola aby som nevyskocila z
												// pola
					if (sampa.elementAt(i).equals("d") && sampa.elementAt(i + 1).equals("E")) {
						sampa.setElementAt("J-", i);
					}
				}
				if (sampa.elementAt(i).equals("dz") && sampa.elementAt(i + 1).equals("k")) {
					sampa.setElementAt("ts", i);
				}

				/* pronouncing j so samohlaskami */
				if (sampa.elementAt(i).equals("I_^")) {
					if (sampa.elementAt(i + 1).equals("a")) {
						sampa.setElementAt("j", i);
					} else if (sampa.elementAt(i + 1).equals("E")) {
						sampa.setElementAt("j", i);
					} else if (sampa.elementAt(i + 1).equals("I")) {
						sampa.setElementAt("j", i);
					} else if (sampa.elementAt(i + 1).equals("O")) {
						sampa.setElementAt("j", i);
					} else if (sampa.elementAt(i + 1).equals("U")) {
						sampa.setElementAt("j", i);
					} else if (sampa.elementAt(i + 1).equals("I~")) {
						sampa.setElementAt("j", i);
					} else if (sampa.elementAt(i + 1).equals("U~")) {
						sampa.setElementAt("j", i);
					} else {
						sampa.setElementAt("I_^", i);
					}
				}

				/* mutation k */
				if (sampa.elementAt(i).equals("k")) {
					if (sampa.elementAt(i + 1).equals("m")) {
						sampa.setElementAt("g", i);
					} else if (sampa.elementAt(i + 1).equals("d")) {
						sampa.setElementAt("g", i);
					} else if (sampa.elementAt(i + 1).equals("(Z)")) {
						sampa.setElementAt("g", i);
					}
				}

				if (i - 2 >= 0) {
					if (sampa.elementAt(i).equals("k")) { // kontrola aby som
															// nevyskocila z
															// pola
						if (sampa.elementAt(i + 1).equals("_")) {
							if (sampa.elementAt(i - 2).equals("(S)")) {
								sampa.setElementAt("g", i);
							} else if (sampa.elementAt(i - 2).equals("v")) {
								sampa.setElementAt("g", i);
							} else if (sampa.elementAt(i - 2).equals("d")) {
								sampa.setElementAt("g", i);
							} else if (sampa.elementAt(i - 2).equals("a")) {
								sampa.setElementAt("g", i);
							} else if (sampa.elementAt(i - 2).equals("l")) {
								sampa.setElementAt("g", i);
							} else if (sampa.elementAt(i - 2).equals("h-")) {
								sampa.setElementAt("g", i);
							} else if (sampa.elementAt(i - 2).equals("r")) {
								sampa.setElementAt("g", i);
							} else if (sampa.elementAt(i - 2).equals("I~")) {
								sampa.setElementAt("g", i);
							}
						} else {
							sampa.setElementAt("k", i);
						}
					}
				}

				/* mutation po pismene l */
				if (i - 4 >= 0) {
					if (sampa.elementAt(i).equals("l") && sampa.elementAt(i + 1).equals("E")) {
						if (sampa.elementAt(i - 1).equals("E")) {
							if (sampa.elementAt(i - 4).equals("r")) {
								sampa.setElementAt("l", i);
							} else if (sampa.elementAt(i - 4).equals("ts")) {
								sampa.setElementAt("l", i);
							} else if (sampa.elementAt(i - 4).equals("_")) {
								sampa.setElementAt("(L)", i);
							} else if (sampa.elementAt(i - 4).equals("b")) {
								sampa.setElementAt("(L)", i);
							} else if (sampa.elementAt(i - 4).equals("n")) {
								sampa.setElementAt("(L)", i);
							} else if (sampa.elementAt(i - 4).equals("s")) {
								sampa.setElementAt("(L)", i);
							} else {
								sampa.setElementAt("l", i);
							}
						}
						if (sampa.elementAt(i - 4).equals("b")) {
							sampa.setElementAt("l", i);
						} else if (sampa.elementAt(i - 4).equals("p")) {
							sampa.setElementAt("l", i);
						} else if (sampa.elementAt(i - 4).equals("n")) {
							sampa.setElementAt("(L)", i);
						} else if (sampa.elementAt(i - 4).equals("U") && sampa.elementAt(i - 1).equals("_")) {
							sampa.setElementAt("l", i);
						} else if (sampa.elementAt(i - 1).equals("O")) {
							if (sampa.elementAt(i - 4).equals("v")) {
								sampa.setElementAt("l", i);
							} else if (sampa.elementAt(i - 4).equals("O")) {
								sampa.setElementAt("l", i);
							} else {
								sampa.setElementAt("(L)", i);
							}
						} else {
							sampa.setElementAt("(L)", i);
						}
					}
				}

				if (sampa.elementAt(i).equals("l") && sampa.elementAt(i + 1).equals("I")) {
					if (sampa.elementAt(i - 1).equals("g")) {
						sampa.setElementAt("l", i);
					} else if (sampa.elementAt(i - 1).equals("p")) {
						sampa.setElementAt("l", i);
					}
				}

				if (i - 4 >= 0) {
					if (sampa.elementAt(i).equals("l") && sampa.elementAt(i + 1).equals("I")) {
						if (sampa.elementAt(i - 1).equals("a")) {
							if (sampa.elementAt(i - 4).equals("_")) {
								sampa.setElementAt("l", i);
							} else if (sampa.elementAt(i - 4).equals("l")) {
								sampa.setElementAt("l", i);
							} else if (sampa.elementAt(i - 4).equals("I")) {
								sampa.setElementAt("l", i);
							} else {
								sampa.setElementAt("(L)", i);
							}
						}

						else if (sampa.elementAt(i - 1).equals("_")) {
							if (sampa.elementAt(i - 4).equals("n")) {
								sampa.setElementAt("l", i);
							} else if (sampa.elementAt(i - 4).equals("s")) {
								sampa.setElementAt("l", i);
							} else if (sampa.elementAt(i - 4).equals("z")) {
								sampa.setElementAt("l", i);
							} else {
								sampa.setElementAt("(L)", i);
							}
						}

						else if (sampa.elementAt(i - 4).equals("k")) {
							sampa.setElementAt("l", i);
						}
					}
				}

				if (i - 4 >= 0) {
					if (sampa.elementAt(i).equals("l") && sampa.elementAt(i + 1).equals("I~")) {
						if (sampa.elementAt(i - 4).equals("l")) {
							sampa.setElementAt("l", i);
						}
						if (sampa.elementAt(i - 4).equals("I")) {
							sampa.setElementAt("l", i);
						} else {
							sampa.setElementAt("(L)", i);
						}
					}
				}

				if (sampa.elementAt(i).equals("l") && sampa.elementAt(i + 1).equals("I~")) {
					if (sampa.elementAt(i - 1).equals("I")) {
						sampa.setElementAt("l", i);
					} else {
						sampa.setElementAt("(L)", i);
					}
				}

				if (sampa.elementAt(i).equals("l")) {
					if (sampa.elementAt(i + 1).equals("n")) {
						if (sampa.elementAt(i - 1).equals("p")) {
							sampa.setElementAt("l=", i);
						} else if (sampa.elementAt(i - 1).equals("s")) {
							sampa.setElementAt("l=", i);
						} else if (sampa.elementAt(i - 1).equals("n")) {
							sampa.setElementAt("l=", i);
						} else {
							sampa.setElementAt("l", i);
						}
					} else if (sampa.elementAt(i + 1).equals("h-")) {
						sampa.setElementAt("l=", i);
					} else if (sampa.elementAt(i + 1).equals("b")) {
						sampa.setElementAt("l=", i);
					} else if (sampa.elementAt(i + 1).equals("(Z)")) {
						sampa.setElementAt("l=", i);
					} else if (sampa.elementAt(i + 1).equals("t") && sampa.elementAt(i - 1).equals("U")) {
						sampa.setElementAt("l", i);
					}
				}

				/* mutation pismena n */
				if (sampa.elementAt(i).equals("n")) {
					if (sampa.elementAt(i + 1).equals("E")) {
						sampa.setElementAt("(J)", i);
					} else if (sampa.elementAt(i + 1).equals("I")) {
						sampa.setElementAt("(J)", i);
					} else if (sampa.elementAt(i + 1).equals("I~")) {
						sampa.setElementAt("(J)", i);
					} else if (sampa.elementAt(i + 1).equals("k")) {
						sampa.setElementAt("(N)", i);
					} else if (sampa.elementAt(i + 1).equals("g")) {
						sampa.setElementAt("(N)", i);
					} else if (sampa.elementAt(i + 1).equals("I_^")) {
						sampa.setElementAt("(N)", i);
					} else {
						sampa.setElementAt("n", i);
					}
				}

				/* mutation pismena r */
				if (sampa.elementAt(i).equals("r")) {
					if (sampa.elementAt(i + 1).equals("d")) {
						sampa.setElementAt("r=", i);
					}
					if (sampa.elementAt(i + 1).equals("(Z)")) {
						sampa.setElementAt("r=", i);
					}
					if (sampa.elementAt(i + 1).equals("h-")) {
						sampa.setElementAt("r=", i);
					}
					if (sampa.elementAt(i + 1).equals("t")) {
						sampa.setElementAt("r", i);
					}
				}

				if (i - 4 >= 0) {
					if (sampa.elementAt(i).equals("r")) {
						if (sampa.elementAt(i + 1).equals("ts")) {
							if (sampa.elementAt(i - 4).equals("E")) {
								sampa.setElementAt("r=", i);
							} else if (sampa.elementAt(i - 4).equals("n")) {
								sampa.setElementAt("r=", i);
							} else if (sampa.elementAt(i - 4).equals("ts")) {
								sampa.setElementAt("r=", i);
							}
						}

						else if (sampa.elementAt(i + 1).equals("v")) {
							if (sampa.elementAt(i - 4).equals("b")) {
								sampa.setElementAt("r=", i);
							} else if (sampa.elementAt(i - 4).equals("z")) {
								sampa.setElementAt("r=", i);
							} else if (sampa.elementAt(i - 4).equals("U_^O")) {
								sampa.setElementAt("r=", i);
							}
						}

						else if (sampa.elementAt(i + 1).equals("p")) {
							if (sampa.elementAt(i - 4).equals("t(S)")) {
								sampa.setElementAt("r=", i);
							}
						}

						else if (sampa.elementAt(i + 1).equals("k")) {
							if (sampa.elementAt(i - 4).equals("k")) {
								sampa.setElementAt("r=", i);
							}
						}

						else if (sampa.elementAt(i + 1).equals("t(S)")) {
							if (sampa.elementAt(i - 4).equals("I")) {
								sampa.setElementAt("r=", i);
							}
						}

						else if (sampa.elementAt(i + 1).equals("(S)")) {
							if (sampa.elementAt(i - 4).equals("l")) {
								sampa.setElementAt("r=", i);
							}
						}
					}
				}

				set = false;
				/* mutation pismena s */
				if ((i + 4 < sampa.size()) && (i - 2 >= 0)) {
					if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 4).equals("z")) {
						if (sampa.elementAt(i - 2).equals("h-")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i - 2).equals("m")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i - 2).equals("E") && sampa.elementAt(i + 3).equals("O")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 4).equals("k")) {
						if (sampa.elementAt(i - 1).equals("_")) {
							if (sampa.elementAt(i - 2).equals("d")) {
								sampa.setElementAt("s", i);
								set = true;
							} else if (sampa.elementAt(i - 2).equals("v")) {
								sampa.setElementAt("s", i);
								set = true;
							} else {
								sampa.setElementAt("z", i);
								s = true;
							}
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("a")) {
						if (sampa.elementAt(i - 2).equals("n") && sampa.elementAt(i + 4).equals("_")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("_")) {
						if (sampa.elementAt(i - 1).equals("_")) {
							if (sampa.elementAt(i - 2).equals("I")) {
								if (sampa.elementAt(i + 4).equals("n")) {
									sampa.setElementAt("z", i);
									s = true;
								} else if (sampa.elementAt(i + 4).equals("d")) {
									sampa.setElementAt("z", i);
									s = true;
								} else if (sampa.elementAt(i + 4).equals("s")) {
									sampa.setElementAt("z", i);
									s = true;
								} else if (sampa.elementAt(i + 4).equals("U")) {
									sampa.setElementAt("z", i);
									s = true;
								}

							} else if (sampa.elementAt(i - 2).equals("U")) {
								if (sampa.elementAt(i + 4).equals("r")) {
									sampa.setElementAt("z", i);
									s = true;
								}
							}
							/*
							 * else if (sampa.elementAt(i-2).equals("0")) { //
							 * nula znamena nejaku medzeru, zatial ju
							 * nepouzivame if (sampa.elementAt(i+4).equals("d"))
							 * {sampa.setElementAt("z", i);} }
							 */
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 2).equals("n")) {
						if (sampa.elementAt(i + 4).equals("O")) {
							if (sampa.elementAt(i - 1).equals("E")) {
								sampa.setElementAt("s", i);
							} else {
								sampa.setElementAt("z", i);
								set = true;
								s = true;
							}
						} else if (sampa.elementAt(i - 1).equals("E")) {
							if (sampa.elementAt(i + 4).equals("b")) {
								sampa.setElementAt("z", i);
								s = true;
							} else if (sampa.elementAt(i + 4).equals("_") && sampa.elementAt(i + 3).equals("E")) {
								sampa.setElementAt("z", i);
								s = true;
							} else {
								sampa.setElementAt("s", i);
							}
						} else if (sampa.elementAt(i + 4).equals("I") && sampa.elementAt(i + 3).equals("n")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 2).equals("l)")) {
						if (sampa.elementAt(i + 4).equals("O")) {
							if (sampa.elementAt(i + 3).equals("k")) {
								sampa.setElementAt("s", i);
							} else {
								sampa.setElementAt("z", i);
								s = true;
							}
						}
					}
				}

				if (i + 4 < sampa.size()) {
					if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 4).equals("t")) {
						if (sampa.elementAt(i - 1).equals("E")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i + 3).equals("E")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 4).equals("(Z)")) {
						if (sampa.elementAt(i + 3).equals("U")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 4).equals("k")) {
						if (sampa.elementAt(i - 1).equals("_")) {
							if (sampa.elementAt(i + 3).equals("v")) {
								sampa.setElementAt("s", i);
							}
							if (set == false) {
								sampa.setElementAt("z", i);
								s = true;
							}
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("a")) {
						if (sampa.elementAt(i + 4).equals("I_^")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i + 4).equals("s")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}
				}

				if ((i + 4 < sampa.size()) && (i - 1 >= 0)) {
					if (sampa.elementAt(i - 1).equals("_")) {
						if (sampa.elementAt(i + 3).equals("l") && sampa.elementAt(i + 4).equals("a")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}
				}

				if ((i + 3 < sampa.size()) && (i - 2 >= 0)) {
					if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("a")) {
						if (sampa.elementAt(i - 1).equals("_")) {
							if (sampa.elementAt(i - 2).equals("I")) {
								sampa.setElementAt("z", i);
								s = true;
							} else if (sampa.elementAt(i - 2).equals("v")) {
								sampa.setElementAt("z", i);
								s = true;
							}
						} else if (sampa.elementAt(i - 2).equals("t")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i - 2).equals("a~")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					} else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("_")) {
						if (sampa.elementAt(i - 2).equals("I~")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("z")) {
						if (sampa.elementAt(i - 2).equals("t(S)")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i - 2).equals("t")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("I")) {
						if (sampa.elementAt(i - 2).equals("O")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i - 2).equals("E") && sampa.elementAt(i + 4).equals("E")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("b")) {
						if (sampa.elementAt(i - 2).equals("I")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i - 1).equals("_")) {
						if (sampa.elementAt(i + 3).equals("a~") && sampa.elementAt(i - 2).equals("E")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i + 3).equals("h-") && sampa.elementAt(i - 2).equals("I_^")) {
							sampa.setElementAt("z", i);
							s = true;
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 2).equals("n")) {
						if (sampa.elementAt(i - 1).equals("E")) {
							if (sampa.elementAt(i + 3).equals("O")) {
								sampa.setElementAt("z", i);
								s = true;
							}
							if (set == false) {
								sampa.setElementAt("s", i);
							}
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 2).equals("t(S)")) {
						if (sampa.elementAt(i - 1).equals("a")) {
							if (sampa.elementAt(i + 3).equals("U")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 3).equals("t(S)")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 3).equals("I~")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 3).equals("I_^")) {
								sampa.setElementAt("s", i);
							} else {
								sampa.setElementAt("z", i);
								s = true;
							}
						}
					}

					else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 2).equals("l)")) {
						if (sampa.elementAt(i - 1).equals("U")) {
							sampa.setElementAt("z", i);
							s = true;
						} else if (sampa.elementAt(i + 3).equals("r")) {
							if (sampa.elementAt(i - 1).equals("a")) {
								sampa.setElementAt("z", i);
								s = true;
							}
						} else if (sampa.elementAt(i + 4).equals("O")) {
							if (sampa.elementAt(i + 3).equals("k")) {
								sampa.setElementAt("s", i);
							} else {
								sampa.setElementAt("z", i);
								s = true;
							}
						}
					}
				}

				if (i + 3 < sampa.size()) {
					if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("t")) {
						sampa.setElementAt("s", i);
					} else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("{")) {
						sampa.setElementAt("z", i);
						s = true;
					} else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i + 3).equals("g")) {
						sampa.setElementAt("z", i);
						s = true;
					} else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 1).equals("a~")) {
						if (sampa.elementAt(i + 3).equals("p")) {
							sampa.setElementAt("z", i);
							s = true;
						} else {
							sampa.setElementAt("s", i);
						}
					} else if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 1).equals("U")) {
						if (sampa.elementAt(i + 3).equals("E")) {
							sampa.setElementAt("z", i);
							s = true;
						} else {
							sampa.setElementAt("s", i);
						}
					}
				}

				if (i - 2 >= 0) {
					if (sampa.elementAt(i).equals("s") && sampa.elementAt(i - 2).equals("m")) {
						sampa.setElementAt("s", i);
					}
				}

				/* mutation */
				if (i - 3 >= 0) {
					if (sampa.elementAt(i).equals("c")) {
						if (sampa.elementAt(i - 3).equals("t")) {
							sampa.setElementAt("J-", i);
						} else if (sampa.elementAt(i - 3).equals("I")) {
							sampa.setElementAt("J-", i);
						} else if (sampa.elementAt(i - 3).equals("a")) {
							sampa.setElementAt("c", i);
						} else if (sampa.elementAt(i - 3).equals("h-")) {
							sampa.setElementAt("J-", i);
						} else if (sampa.elementAt(i - 3).equals("r")) {
							sampa.setElementAt("J-", i);
						} else {
							sampa.setElementAt("c", i);
						}
					}
				}

				/* mutation t a vynimky, kedy sa te, ti cita tvrdo */
				if (d == false) {
					if (i + 3 < sampa.size()) {
						if (sampa.elementAt(i).equals("t") && sampa.elementAt(i + 1).equals("E")) {
							if (sampa.elementAt(i - 1).equals("_")) {
								if (sampa.elementAt(i + 3).equals("O")) {
									sampa.setElementAt("c", i);
								} else if (sampa.elementAt(i + 3).equals("I~")) {
									sampa.setElementAt("c", i);
								}
							}

							else if (sampa.elementAt(i + 3).equals("I")) {
								if (sampa.elementAt(i - 1).equals("a")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							}

							else if (sampa.elementAt(i + 3).equals("_")) {
								if (sampa.elementAt(-1).equals("O")) {
									sampa.setElementAt("c", i);
								} else if (sampa.elementAt(-1).equals("a")) {
									sampa.setElementAt("c", i);
								} else if (sampa.elementAt(-1).equals("I")) {
									sampa.setElementAt("c", i);
								}
							}

							else if (sampa.elementAt(i + 3).equals("a")) {
								if (sampa.elementAt(i - 1).equals("O")) {
									sampa.setElementAt("t", i);
									set = true;
								} else if (sampa.elementAt(i - 1).equals("I")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							}

							else if (sampa.elementAt(i + 3).equals("O")) {
								if (sampa.elementAt(i - 1).equals("O")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							}

							else if (sampa.elementAt(i - 1).equals("E")) {
								if (sampa.elementAt(i + 3).equals("E")) {
									sampa.setElementAt("t", i);
									set = true;
								} else if (sampa.elementAt(i + 3).equals("r")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							} else if (sampa.elementAt(i + 3).equals("(S)")) {
								sampa.setElementAt("t", i);
								set = true;
							} else {
								sampa.setElementAt("c", i);
							}
						}
					}

					if (sampa.elementAt(i).equals("t") && sampa.elementAt(i + 1).equals("E")) {
						if (sampa.elementAt(i - 1).equals("v")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (sampa.elementAt(i - 1).equals("(S)")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (sampa.elementAt(i + 1).equals("b")) {
							sampa.setElementAt("t", i);
						} else if (set == false) {
							sampa.setElementAt("c", i);
						} // ak niekde nastala vynimka tvrdeho citania,
					} // toto je kontrola aby sa to neprepisalo na makke t

					set = false;

					if (i + 3 < sampa.size()) {
						if (sampa.elementAt(i).equals("t") && sampa.elementAt(i + 1).equals("I")) {
							if (sampa.elementAt(i + 3).equals("k")) {
								if (sampa.elementAt(i - 1).equals("h-")) {
									sampa.setElementAt("c", i);
								} else {
									sampa.setElementAt("t", i);
									set = true;
								}
							}

							else if (sampa.elementAt(i + 3).equals("O")) {
								if (sampa.elementAt(i - 1).equals("a")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							}

							else if (sampa.elementAt(i - 1).equals("U")) {
								if (sampa.elementAt(i + 3).equals("U")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							}

							else if (sampa.elementAt(i + 3).equals("a")) {
								if (sampa.elementAt(i - 1).equals("O")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							} else {
								sampa.setElementAt("c", i);
							}
						}

						if (sampa.elementAt(i).equals("t") && sampa.elementAt(i + 1).equals("I~")) {
							if (sampa.elementAt(i + 3).equals("n")) {
								if (sampa.elementAt(i - 1).equals("s")) {
									sampa.setElementAt("c", i);
								} else if (sampa.elementAt(i - 1).equals("I")) {
									sampa.setElementAt("t", i);
									set = true;
								} else if (sampa.elementAt(i - 1).equals("n")) {
									sampa.setElementAt("t", i);
									set = true;
								} else if (sampa.elementAt(i - 1).equals("a")) {
									sampa.setElementAt("t", i);
									set = true;
								} else {
									sampa.setElementAt("c", i);
								}
							} else if (sampa.elementAt(i + 3).equals("I")) {
								sampa.setElementAt("t", i);
								set = true;
							} else {
								sampa.setElementAt("c", i);
							}
						}
					}

					if (sampa.elementAt(i).equals("t") && sampa.elementAt(i + 1).equals("I")) {
						if (sampa.elementAt(i - 1).equals("(S)")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (sampa.elementAt(i - 1).equals("k")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (sampa.elementAt(i - 1).equals("r")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (sampa.elementAt(i - 1).equals("p")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (set == false) {
							sampa.setElementAt("c", i);
						}
					}

					if (sampa.elementAt(i).equals("t") && sampa.elementAt(i + 1).equals("I~")) {
						if (sampa.elementAt(i - 1).equals("k")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (sampa.elementAt(i - 1).equals("(S)")) {
							sampa.setElementAt("t", i);
							set = true;
						} else if (set == false) {
							sampa.setElementAt("c", i);
						}
					}

					if (i + 3 < sampa.size()) {
						if (sampa.elementAt(i).equals("t")) {
							if (sampa.elementAt(i + 1).equals("_")) {
								if (sampa.elementAt(i + 3).equals("a")) {
									sampa.setElementAt("d", i);
								} else if (sampa.elementAt(i + 3).equals("k")) {
									sampa.setElementAt("d", i);
								} else if (sampa.elementAt(i + 3).equals("l")) {
									sampa.setElementAt("d", i);
								} else if (sampa.elementAt(i - 1).equals("O")) {
									if (sampa.elementAt(i + 3).equals("b")) {
										sampa.setElementAt("t", i);
									} else {
										sampa.setElementAt("d", i);
									}
								}
							}
						}
					}

					if (i == (sampa.size() - 2)) { // kontrola aby som
													// nevyskocila z pola
						if (sampa.elementAt(i).equals("t") && sampa.elementAt(i + 1).equals("E")) {
							sampa.setElementAt("J-", i);
						}
					}
				}

				/* mutation u */
				if (sampa.elementAt(i).equals("U")) {
					if (sampa.elementAt(i - 1).equals("O")) {
						sampa.setElementAt("U_^", i);
					}
				}

				/* mutation v */
				if (sampa.elementAt(i).equals("v")) {
					if (sampa.elementAt(i + 1).equals("_")) {
						if (sampa.elementAt(i - 1).equals("O")) {
							sampa.setElementAt("U_^", i);
						} else if (sampa.elementAt(i - 1).equals("a")) {
							sampa.setElementAt("U_^", i);
						}
					} else if (sampa.elementAt(i + 1).equals("(S)")) {
						sampa.setElementAt("f", i);
					} else if (sampa.elementAt(i + 1).equals("n")) {
						if (sampa.elementAt(i - 1).equals("z")) {
							sampa.setElementAt("w", i);
						} else if (sampa.elementAt(i - 1).equals("_")) {
							sampa.setElementAt("w", i);
						}
					} else if (sampa.elementAt(i + 1).equals("ts")) {
						sampa.setElementAt("U_^", i);
					} else if (sampa.elementAt(i + 1).equals("(S)")) {
						sampa.setElementAt("f", i);
					} else if (sampa.elementAt(i + 1).equals("z")) {
						sampa.setElementAt("w", i);
					} else if (sampa.elementAt(i + 1).equals("t")) {
						sampa.setElementAt("f", i);
					} else if (sampa.elementAt(i + 1).equals("b")) {
						sampa.setElementAt("U_^", i);
					} else if (sampa.elementAt(i + 1).equals("h-")) {
						sampa.setElementAt("w", i);
					} else if (sampa.elementAt(i + 1).equals("d")) {
						sampa.setElementAt("U_^", i);
					} else if (sampa.elementAt(i + 1).equals("(Z)")) {
						sampa.setElementAt("w", i);
					} else if (sampa.elementAt(i + 1).equals("m")) {
						sampa.setElementAt("U_^", i);
					} else if (sampa.elementAt(i + 1).equals("t(S)")) {
						sampa.setElementAt("f", i);
					} else if (sampa.elementAt(i + 1).equals("p")) {
						sampa.setElementAt("f", i);
					}
				}

				if (i + 2 < sampa.size()) {
					if (sampa.elementAt(i).equals("v")) {
						if (sampa.elementAt(i + 1).equals("_")) {
							if (sampa.elementAt(i + 2).equals("p")) {
								sampa.setElementAt("f", i);
							} else if (sampa.elementAt(i + 2).equals("t")) {
								sampa.setElementAt("f", i);
							} else if (sampa.elementAt(i + 2).equals("s")) {
								sampa.setElementAt("f", i);
							} else if (sampa.elementAt(i + 2).equals("k")) {
								sampa.setElementAt("f", i);
							} else if (sampa.elementAt(i + 2).equals("t(S)")) {
								sampa.setElementAt("f", i);
							} else if (sampa.elementAt(i + 2).equals("r")) {
								sampa.setElementAt("v", i);
							} else if (sampa.elementAt(i + 2).equals("O")) {
								sampa.setElementAt("v", i);
							} else if (sampa.elementAt(i + 2).equals("ts")) {
								sampa.setElementAt("f", i);
							} else if (sampa.elementAt(i + 2).equals("E")) {
								sampa.setElementAt("v", i);
							} else if (sampa.elementAt(i + 2).equals("l")) {
								sampa.setElementAt("v", i);
							}
						} else if (sampa.elementAt(i + 1).equals("k")) {
							if (sampa.elementAt(i + 2).equals("_")) {
								sampa.setElementAt("f", i);
							} else {
								sampa.setElementAt("U_^", i);
							}
						}
					}
				}

				if (i - 3 >= 0) {
					if (sampa.elementAt(i).equals("v")) {
						if (sampa.elementAt(i + 1).equals("s")) {
							if (sampa.elementAt(i - 3).equals("l")) {
								sampa.setElementAt("f", i);
							} else {
								sampa.setElementAt("U_^", i);
							}
						}
					}
				}

				/* mutation z */
				if (sampa.elementAt(i).equals("z") && s == false) {
					if (sampa.elementAt(i + 1).equals("k")) {
						sampa.setElementAt("s", i);
					} else if (sampa.elementAt(i + 1).equals("p")) {
						sampa.setElementAt("s", i);
					} else if (sampa.elementAt(i + 1).equals("(S)")) {
						sampa.setElementAt("s", i);
					} else if (sampa.elementAt(i + 1).equals("c")) {
						sampa.setElementAt("s", i);
					}
				}

				if (i + 2 < sampa.size()) {
					if (sampa.elementAt(i).equals("z") && s == false) {
						if (sampa.elementAt(i + 1).equals("_")) {
							if (sampa.elementAt(i + 2).equals("t")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 2).equals("t(S)")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 2).equals("t")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 2).equals("p")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 2).equals("f")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 2).equals("s")) {
								sampa.setElementAt("s", i);
							} else if (sampa.elementAt(i + 2).equals("k")) {
								sampa.setElementAt("s", i);
							} else {
								sampa.setElementAt("z", i);
							}
						}
					}
				}

				/* mutation ž */
				if (sampa.elementAt(i).equals("(Z)")) {
					if (sampa.elementAt(i + 1).equals("k")) {
						sampa.setElementAt("(S)", i);
					} else if (sampa.elementAt(i + 1).equals("s")) {
						sampa.setElementAt("(S)", i);
					} else if (sampa.elementAt(i + 1).equals("(S)")) {
						sampa.setElementAt("(S)", i);
					}
				}

				if (i - 4 >= 0) {
					if (sampa.elementAt(i).equals("(Z)")) {
						if (sampa.elementAt(i + 1).equals("_")) {
							if (sampa.elementAt(i - 4).equals("a")) {
								sampa.setElementAt("(S)", i);
							} else if (sampa.elementAt(i - 4).equals("I")) {
								sampa.setElementAt("(S)", i);
							} else if (sampa.elementAt(i - 4).equals("l")) {
								sampa.setElementAt("(S)", i);
							} else if (sampa.elementAt(i - 4).equals("m")) {
								sampa.setElementAt("(S)", i);
							} else if (sampa.elementAt(i - 4).equals("t")) {
								sampa.setElementAt("(Z)", i);
							} else if (sampa.elementAt(i - 4).equals("n")) {
								sampa.setElementAt("(S)", i);
							} else {
								sampa.setElementAt("(Z)", i);
							}
						}
					}
				}
				i++;
			}
			return sampa;
		}

		/**
		 * was used in mobile j2me
		 * 
		 * @param sampa
		 * @return
		 */
		public String getCharFromSampa(String sampa) {
			String c = "_";
			if (sampa.compareTo("a") == 0) {
				c = "a";
			} else if (sampa.compareTo("E") == 0) {
				c = "e";
			} else if (sampa.compareTo("I") == 0) {
				c = "i";
			} else if (sampa.compareTo("O") == 0) {
				c = "o";
			} else if (sampa.compareTo("U") == 0) {
				c = "u";
			} else if (sampa.compareTo("ä") == 0) {
				c = "ä";
			} else if (sampa.compareTo("a~") == 0) {
				c = "á";
			} else if (sampa.compareTo("E~") == 0) {
				c = "é";
			} else if (sampa.compareTo("I~") == 0) {
				c = "í";
			} else if (sampa.compareTo("O~") == 0) {
				c = "ó";
			} else if (sampa.compareTo("U~") == 0) {
				c = "ú";
			} else if (sampa.compareTo("I_^a") == 0) {
				c = "í";
			} else if (sampa.compareTo("I_^E") == 0) {
				c = "í";
			} else if (sampa.compareTo("I_^U") == 0) {
				c = "í";
			} else if (sampa.compareTo("U_^O") == 0) {
				c = "ô";
			} else if (sampa.compareTo("p") == 0) {
				c = "p";
			} else if (sampa.compareTo("b") == 0) {
				c = "b";
			} else if (sampa.compareTo("t") == 0) {
				c = "t";
			} else if (sampa.compareTo("d") == 0) {
				c = "d";
			} else if (sampa.compareTo("c") == 0) {
				c = "c";
			} else if (sampa.compareTo("J-") == 0) {
				c = "ď";
			} else if (sampa.compareTo("kk") == 0) {
				c = "k";
			} else if (sampa.compareTo("g") == 0) {
				c = "g";
			} else if (sampa.compareTo("ts") == 0) {
				c = "c";
			} else if (sampa.compareTo("dz") == 0) {
				c = "c";
			} else if (sampa.compareTo("t(S)") == 0) {
				c = "č";
			} else if (sampa.compareTo("d(Z)") == 0) {
				c = "č";
			} else if (sampa.compareTo("f") == 0) {
				c = "f";
			} else if (sampa.compareTo("v") == 0) {
				c = "v";
			} else if (sampa.compareTo("w") == 0) {
				c = "w";
			} else if (sampa.compareTo("U_^") == 0) {
				c = "v";
			} else if (sampa.compareTo("s") == 0) {
				c = "s";
			} else if (sampa.compareTo("z") == 0) {
				c = "z";
			} else if (sampa.compareTo("(S)") == 0) {
				c = "š";
			} else if (sampa.compareTo("(Z)") == 0) {
				c = "ž";
			} else if (sampa.compareTo("x") == 0) {
				c = "x";
			} else if (sampa.compareTo("h-") == 0) {
				c = "h";
			} else if (sampa.compareTo("G") == 0) {
				c = "h";
			} else if (sampa.compareTo("j") == 0) {
				c = "j";
			} else if (sampa.compareTo("I_^") == 0) {
				c = "j";
			} else if (sampa.compareTo("r") == 0) {
				c = "r";
			} else if (sampa.compareTo("r=") == 0) {
				c = "r";
			} else if (sampa.compareTo("r=:") == 0) {
				c = "r";
			} else if (sampa.compareTo("l=") == 0) {
				c = "l";
			} else if (sampa.compareTo("l=:") == 0) {
				c = "l";
			} else if (sampa.compareTo("(L)") == 0) {
				c = "l";
			} else if (sampa.compareTo("m") == 0) {
				c = "m";
			} else if (sampa.compareTo("F") == 0) {
				c = "f";
			} else if (sampa.compareTo("n") == 0) {
				c = "n";
			} else if (sampa.compareTo("(N)") == 0) {
				c = "n";
			} else if (sampa.compareTo("(J)") == 0) {
				c = "n";
			}
			return c;
		}

	}

}