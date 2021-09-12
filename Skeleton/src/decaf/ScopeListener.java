package decaf;

import java.util.Arrays;
import java.util.Objects;

public class ScopeListener extends DecafParserBaseListener {
	SymbolTable mySymbolTable = new SymbolTable();
	boolean main_method_found;
	boolean main_method_parameter;
	
	public ScopeListener() {}
	
	public void enterProgram(DecafParser.ProgramContext ctx) {
		mySymbolTable.enterScope();
		main_method_found = false;
		main_method_parameter = false;
		
		String name = ctx.ID().getText();
		System.out.println(name);
		int type = DecafLexer.CLASS;
		int line = ctx.getStart().getLine();
		ScopeElement myScopeElement = new ScopeElement(name, type, line);
		mySymbolTable.addId(name, myScopeElement);
	}
	
	public void enterMethod_decl(DecafParser.Method_declContext ctx) {
		String name = ctx.ID(0).getText();
		System.out.println(name);
		if (name.equals("main")) {
			main_method_found = true;
			
			if (ctx.ID().size() > 1) {
				main_method_parameter = true;			
			}
		}		
		
		int psize = ctx.ID().size() - 1;	
		int[] plist = new int [psize];
		
		for (int i = 0; i < psize; i++) {
			String pname = ctx.ID(i + 1).getText();
			int ptype = (ctx.type(i + 1).INT() != null)? DecafParser.INT : DecafParser.BOOLEAN ;
			plist[i] = ptype;
			int line = ctx.getStart().getLine();
			
			mySymbolTable.addId(name, new ScopeElement(pname, line, plist));		
		}
	}
	
	public void enterField_decl(DecafParser.Field_declContext ctx) { 
		int field_count = ctx.field_name().size();
		int line = ctx.getStart().getLine();
		int type;
		
		if (ctx.type().INT() != null)
			type = DecafParser.INT;
		else 
			type = DecafParser.BOOLEAN;
		
		// this checks whether an ID is used twice:
		for (int i = 0; i < field_count; i++) {
			DecafParser.Field_nameContext field_name_ctx = ctx.field_name(i);
			String name = field_name_ctx.ID().getText();
			
			ScopeElement scope_element = (ScopeElement)mySymbolTable.probe(name);
			if (scope_element != null)
				System.out.println("Error at line " + line + ": variable " + name + 
						" has already been declared at line " + scope_element.getLine() + ".");
			else
				mySymbolTable.addId(name, new ScopeElement(name, type, line));
		}
	}
	
	public void enterField_name(DecafParser.Field_nameContext ctx) {
		if (ctx.INT_LITERAL() != null) {
			String name = ctx.ID().getText();
			int line = ctx.getStart().getLine();
			int array_size = Integer.parseInt(ctx.INT_LITERAL().getText());
			System.out.println(array_size);
			
			if (array_size == 0)
				System.out.println("Error at line " + line + ": array '" + name
						+ "' cannot have length <= 0.");
		}
	}	
	
	public void enterLocation(DecafParser.LocationContext ctx) {
		String name = ctx.ID().getText();
		int line = ctx.getStart().getLine();
		
		// this checks if an identifier has been declared before use:
		ScopeElement scope_element = (ScopeElement)mySymbolTable.probe(name);
		if (Objects.equals(scope_element, null))
			System.out.println("Error at line " + line + ": variable '" + name + 
					"' has not been declared.");
	}
	
	public void enterMethod_call(DecafParser.Method_callContext ctx) {
		String name = ctx.method_name().ID().getText();
		ScopeElement scope_element = (ScopeElement)mySymbolTable.probe(name);
		int element_size = scope_element.ptypes.length;
		int paramater_size = ctx.expr().size();
		int line = ctx.getStart().getLine();
		int[] method_array = scope_element.ptypes;
		int[] paramater_array = new int[paramater_size];
		// if the number of element in the method declaration and method call are the same:
		if (element_size == paramater_size) {
			// this checks the types of each argument in the method call and adds them to an array
			for (int i = 0; i < paramater_size; i++) {			
				if(ctx.expr(i).location() != null ) {
					ScopeElement paramater_scope_element = (ScopeElement)mySymbolTable.probe(ctx.expr(i).location().ID().getText());
					int type = paramater_scope_element.type;
					paramater_array[i] = type;
				}
				else if(ctx.expr(i).method_call() != null ) {
					ScopeElement paramater_scope_element = (ScopeElement)mySymbolTable.probe(ctx.expr(i).method_call().method_name().ID().getText());
					int type = paramater_scope_element.type;
					paramater_array[i] = type;
				}
				if(ctx.expr(i).literal() != null ) {
					if(ctx.expr(i).literal().INT_LITERAL() != null) {
						int type = DecafParser.INT;
						paramater_array[i] = type;
					}
					else if(ctx.expr(i).literal().bool_literal() != null) {
						int type = DecafParser.BOOLEAN;
						paramater_array[i] = type;
					}
				}
			}
			// if the elements in the arrays are not equal:
			if (!Arrays.equals(method_array, paramater_array)) {
				System.out.println("Error at line " + line + ": the type of arguments in the method call '"
						+ name + "' does not match the type of arguments declared.");
			}
				
		} else { 
			System.out.println("Error at line " + line + ": the number of arguments in the method call '"
					+ name + "' does not match the number of arguments declared.");
		}
	}
	
	public void enterExpr(DecafParser.ExprContext ctx) {
		int exprSize = ctx.expr().size();
		for(int i = 0; i < exprSize; i++) {
			if(ctx.expr(i).method_call() != null) {
				ScopeElement paramater_scope_element = (ScopeElement)mySymbolTable.probe(ctx.expr(i).method_call().method_name().ID().getText());
				String name = ctx.expr(i).method_call().method_name().ID().getText();
				int line = ctx.getStart().getLine();
				
				if(paramater_scope_element.getType() == DecafLexer.VOID) {
					System.out.println("Error at line " + line + ": The method '" + name + "' used in the expression is a void and does not return a value");	
				}
			}
		}
	}
		
	public void exitProgram(DecafParser.ProgramContext ctx) {
		if (!main_method_found) {
			System.out.println("Error: main() method not found in program!");
		}
		if (main_method_parameter) {
			System.out.println("Error: main() must not have any parameters.");
		}
	}

}
